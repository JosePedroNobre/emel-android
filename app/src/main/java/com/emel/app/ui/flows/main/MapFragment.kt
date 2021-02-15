package com.emel.app.ui.flows.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.emel.app.R
import com.emel.app.network.api.adapter.Status
import com.emel.app.network.api.requests.TokenRequest
import com.emel.app.network.model.Malfunction
import com.emel.app.network.model.ParkingMeter
import com.emel.app.ui.base.BaseFragment
import com.emel.app.ui.common.NavigationManager
import com.emel.app.utils.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputLayout
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*
import javax.inject.Inject

//0 = GREEN, 1 = YELLOW, 2 = RED , 3 = GREY

class MapFragment : BaseFragment<MapFragmentVM>(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
    GoogleApiClient.ConnectionCallbacks {

    companion object {
        fun newInstance() = MapFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationManager: NavigationManager

    private lateinit var mGoogleMap: GoogleMap

    private var mCurrLocationMarker: Marker? = null
    private var location: SimpleLocation? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    val LOCATION_PERMISSION = 99
    private var parkingMeters: List<ParkingMeter> = emptyList()
    private var googleApiClient: GoogleApiClient? = null


    private val AUTOCOMPLETE_REQUEST_CODE = 1

    // Set the fields to specify which types of place data to
    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

    // Start the autocomplete intent.
    val intentBuilder = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)


    override fun layoutToInflate() = R.layout.fragment_map

    override fun defineViewModel() =
        ViewModelProviders.of(this, viewModelFactory).get(MapFragmentVM::class.java)

    override fun doOnCreated() {
        requireActivity().backButton.visibility = View.GONE
        requireActivity().tasksIcon.visibility = View.VISIBLE

        googleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()

        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            setLayoutLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION
            )
        }

        val location: Location? = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (location != null) {
            longitude = location.longitude
            latitude = location.latitude
            moveMap()
        }

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyDv0fAgprCw4jXuKghO-PXQQFa78vzB6uw");
        }

        searchAddress.setOnClickListener {
            startActivityForResult(intentBuilder.build(requireContext()), AUTOCOMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        this.latitude = place.latLng?.latitude ?: 0.0
                        this.longitude = place.latLng?.longitude ?: 0.0
                        moveMap()
                        Log.i("test", "Place: ${place.name}, ${place.id}, ${place.id}")

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("test", status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun getMarkers(zoomIntoLocation: Boolean = true) {
        val token = requireActivity().getToken()
        parkingMeters = emptyList()
        viewModel.getParkingMeters(token!!).observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data != null) {
                        parkingMeters = it.data
                        drawPinpoint(zoomIntoLocation)
                    }
                    LoadingUtils.dismiss()
                }
                Status.LOADING -> LoadingUtils.showLoading(childFragmentManager)
                Status.ERROR -> {
                    LoadingUtils.dismiss()
                    if (it.code == 401) {
                        val refreshTokenRequest =
                            TokenRequest(requireActivity().getRefreshToken().toString())

                        viewModel.refreshToken(
                            requireActivity().getToken().toString(),
                            refreshTokenRequest
                        )
                            .observeForever {
                                when (it.status) {
                                    Status.SUCCESS -> {
                                        requireActivity().clearSharedPreferences()
                                        requireActivity().setToken("Bearer ${it?.data?.token}")
                                        requireActivity().setRefreshToken("${it?.data?.refreshToken}")
                                        getMarkers()
                                    }
                                    Status.LOADING -> Toast.makeText(
                                        requireContext(),
                                        "Refreshing Token",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Status.ERROR -> {
                                        logout()
                                    }
                                }
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error while fetching parking meters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        location?.beginUpdates()
    }

    override fun onStart() {
        googleApiClient?.connect()
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        location?.endUpdates()
    }

    override fun onStop() {
        super.onStop()
        googleApiClient?.disconnect();
    }

    private fun checkLocationPermission() {
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED)
        ) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please go to setting to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ -> }
                    .create()
                    .show()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        setLayoutLocation()
                    }
                } else {
                    checkLocationPermission()
                }
                return
            }
        }
    }

    private fun drawPinpoint(zoomIntoLocation: Boolean = false) {
        val currentLatLng = LatLng(latitude, longitude)
        parkingMeters.forEach {
            val latLng = LatLng(it.latitude, it.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Position")

            when (it.status) {
                0 -> {

                    markerOptions.icon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_green
                        )
                    )
                }
                1 -> {
                    markerOptions.icon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_yellow
                        )
                    )
                }
                2 -> {
                    markerOptions.icon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_red

                        )
                    )
                }
                3 -> {
                    markerOptions.icon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_grey

                        )
                    )
                }
            }

            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)
            mCurrLocationMarker!!.tag = it.id
        }

        // move camera
        if (zoomIntoLocation) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 1f))
        }

    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geocoder.getFromLocation(latitude, longitude, 1)

        return if (address.isNotEmpty()) {
            address[0].getAddressLine(0)
        } else {
            ""
        }
    }

    private fun setLayoutLocation() {
        mapview.onCreate(null)
        mapview.getMapAsync(this)
        mapview.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.style_json
            )
        )

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        getMarkers()
        googleMap.setOnMapClickListener(this)
        googleMap.setOnMarkerClickListener(this)
        mGoogleMap.isMyLocationEnabled = true

        val locationButton = (mapview
            .parent as View).findViewById<View>("2".toInt())
        val rlp =
            locationButton.layoutParams as RelativeLayout.LayoutParams
        val ruleList = rlp.rules
        for (i in ruleList.indices) {
            rlp.removeRule(i)
        }
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
    }

    private fun moveMap() {
        val latLng = LatLng(latitude, longitude)
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18f))
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        manageBottomSheet(!bottomSheetLayout.isExpended(), marker)
        return true
    }

    private fun manageBottomSheet(open: Boolean, marker: Marker) {
        if (open) {
            bottomSheetLayout.visibility = View.VISIBLE
            bottomSheetLayout.expand()

            val parkingMeter = parkingMeters.find {
                it.id == marker.tag as Int
            }

            when (parkingMeter?.status) {
                0 -> {
                    subtitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorAccent
                        )
                    )

                    createRepair.setBackgroundResource(R.drawable.rounded_button_red)
                    solveRepair.setBackgroundResource(R.drawable.rounded_button_grey)

                    createRepair.setOnClickListener {
                        createDescriptionDialog(parkingMeter)
                    }

                    subtitle.text = "OPERACIONAL"
                    repairDescriptionTitle.visibility = View.GONE
                    repairDescription.visibility = View.GONE
                }
                2 -> {
                    subtitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.redColor
                        )
                    )

                    createRepair.setBackgroundResource(R.drawable.rounded_button_grey)
                    solveRepair.setBackgroundResource(R.drawable.rounded_button_green)

                    solveRepair.setOnClickListener {
                        solveRepair(parkingMeter)
                    }

                    subtitle.text = "AVARIADO"

                    if (!parkingMeter.malfunctions.isNullOrEmpty()) {
                        repairDescription.text = parkingMeter.malfunctions!![0].description
                        repairDescriptionTitle.visibility = View.VISIBLE
                        repairDescription.visibility = View.VISIBLE
                    }
                }

                3 -> {
                    subtitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorGrey
                        )
                    )

                    createRepair.setBackgroundResource(R.drawable.rounded_button_grey)
                    solveRepair.setBackgroundResource(R.drawable.rounded_button_grey)
                    subtitle.text = "Desactivo"
                }

                1 -> {
                    subtitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorOrange
                        )
                    )

                    createRepair.setBackgroundResource(R.drawable.rounded_button_grey)
                    solveRepair.setBackgroundResource(R.drawable.rounded_button_green)
                    subtitle.text = "EM REPARAÇÃO"

                    if (!parkingMeter.malfunctions.isNullOrEmpty()) {
                        repairDescription.text = parkingMeter.malfunctions[0].description
                    }

                    if (!parkingMeter.malfunctions.isNullOrEmpty()) {
                        repairDescription.text = parkingMeter.malfunctions!![0].description
                        repairDescriptionTitle.visibility = View.VISIBLE
                        repairDescription.visibility = View.VISIBLE
                    }

                    solveRepair.setOnClickListener {
                        solveRepair(parkingMeter)
                    }
                }
            }
        } else {
            bottomSheetLayout.collapse()
            bottomSheetLayout.visibility = View.GONE
        }
    }

    private fun createDescriptionDialog(parkingMeter: ParkingMeter) {
        val textInputLayout = TextInputLayout(requireContext())
        textInputLayout.setPadding(
            19, // if you look at android alert_dialog.xml, you will see the message textview have margin 14dp and padding 5dp. This is the reason why I use 19 here
            10,
            19,
            0
        )
        val input = EditText(context)
        val imm: InputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        textInputLayout.hint = "Inserir Descrição"
        textInputLayout.addView(input)

        val alert = AlertDialog.Builder(requireContext())
            .setView(textInputLayout)
            .setPositiveButton("Criar") { dialog, _ ->

                val malfunction = Malfunction(
                    id = null,
                    creationDate = null,
                    resolvedDate = null,
                    status = 0,
                    description = input.text.toString(),
                    latitude = null,
                    longitude = null,
                    parkingMeterId = parkingMeter.id,
                    applicationUserId = null
                )

                createMalfunction(malfunction, imm)
                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.create()

        alert.show()
    }


    private fun getUserId(): String? {
        val parsedJWT = requireContext().getToken()?.replace("Bearer ", "")?.let { JWT(it) }
        val subscriptionMetaData: Claim = parsedJWT!!.getClaim("id")
        return subscriptionMetaData.asString()
    }

    private fun solveRepair(parkingMeter: ParkingMeter) {

        if (!parkingMeter.malfunctions.isNullOrEmpty() && parkingMeter.malfunctions.first().applicationUserId != null && parkingMeter.malfunctions.first().applicationUserId?.equals(
                getUserId()
            )!!
        ) {
            parkingMeter.status = 0

            viewModel.updateMalfunctionInParkingMeter(
                requireActivity().getToken()!!,
                parkingMeter.id,
                parkingMeter
            ).observe(
                this,
                androidx.lifecycle.Observer { malfunctionUpdateResponse ->

                    when (malfunctionUpdateResponse.status) {
                        Status.SUCCESS -> {
                            bottomSheetLayout.collapse()
                            bottomSheetLayout.visibility = View.GONE
                            getMarkers(false)
                        }
                        Status.LOADING -> print("Loading")
                        Status.ERROR -> {
                            if (malfunctionUpdateResponse.code == 401) {
                                val refreshTokenRequest =
                                    TokenRequest(
                                        requireActivity().getRefreshToken().toString()
                                    )

                                viewModel.refreshToken(
                                    requireActivity().getToken().toString(),
                                    refreshTokenRequest
                                )
                                    .observeForever {
                                        when (it.status) {
                                            Status.SUCCESS -> {
                                                requireActivity().clearSharedPreferences()
                                                requireActivity().setToken("Bearer ${it?.data?.token}")
                                                requireActivity().setRefreshToken("${it?.data?.refreshToken}")
                                                solveRepair(parkingMeter)

                                            }
                                            Status.LOADING -> Toast.makeText(
                                                requireContext(),
                                                "Refreshing Token",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Status.ERROR -> {
                                                logout()
                                            }
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Error while solving the repair",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }
                })
        } else {
            Toast.makeText(requireContext(), "Can't repair this parking meter", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun createMalfunction(malfunction: Malfunction, imm: InputMethodManager) {
        viewModel.addMaintenanceInParkingMeter(requireActivity().getToken()!!, malfunction)
            .observe(this,
                androidx.lifecycle.Observer {
                    when (it.status) {
                        Status.SUCCESS -> {
                            bottomSheetLayout.collapse()
                            bottomSheetLayout.visibility = View.GONE
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                            getMarkers(false)
                        }
                        Status.LOADING -> print("Loading")
                        Status.ERROR -> {
                            if (it.code == 401) {
                                val refreshTokenRequest =
                                    TokenRequest(
                                        requireActivity().getRefreshToken().toString()
                                    )

                                viewModel.refreshToken(
                                    requireActivity().getToken().toString(),
                                    refreshTokenRequest
                                )
                                    .observeForever {
                                        when (it.status) {
                                            Status.SUCCESS -> {
                                                requireActivity().clearSharedPreferences()
                                                requireActivity().setToken("Bearer ${it?.data?.token}")
                                                requireActivity().setRefreshToken("${it?.data?.refreshToken}")
                                                createMalfunction(malfunction, imm)
                                            }
                                            Status.LOADING -> Toast.makeText(
                                                requireContext(),
                                                "Refreshing Token",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Status.ERROR -> {
                                                logout()
                                            }
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Error while creating the repair",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }
                })
    }

    private fun logout() {
        requireActivity()
        navigationManager.goToAuthentication(requireContext())
        requireActivity().finish()
        Toast.makeText(
            requireContext(),
            "Error you have been logged out",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onMapClick(p0: LatLng?) {
        bottomSheetLayout.visibility = View.GONE
    }
}