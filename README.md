# StarterProject
As you all may know, starting an android project is really a pain, you need to setup ```dagger```,```flavors```, ```dependencies```, configure several ```libraries``` sacrifice a goat and two kittens as the meme says. 

This repository aims to kickoff a new android project and for that its packed with the new and latest ```android architecture components```using the following libraries

   * Room database
   * LiveData
   * Paging Library (Loading partial data on demand reduces usage of network bandwidth and system resources)
   * 100% Kotlin
   * Dagger2 for dependency injection
   * Retrofit2
   * ViewModels
   * MVVM (Model-View-ViewModel)
   * MultiDexApplication
   * Ktlint to format your code and give you warnings when some code does not look good :)

This project also offers you: 

* ```Google standard package namings and placements```
* ```Real world professional utilities methods and prepared classes to facilitate your life```
* ```Some view components that you can use like: Chart components, Selectors, Currency inputs etc...```

Using dagger may be confusing to some android beginners so this base project puts all the Dagger configuration at place and also offers 

   * A simple view model injection example ( A viewModel is injected in an Activity and in a Fragment)
   * A simple Injection on retrofit providers
   * A simple Injection on Repositories providers
   * A simple Injection on Database providers
   * A simple Injection on Utility methods

```How to begin?```

* Change project name and package

* Delete in the UI directories "flows" and "components" that you dont want.

* Change your flavours at your taste

* Note: When create/delete your activites,fragments,repositories,view models, network and utility methods make shure you put /remove them on your DI Modules of the app.

* Choose if you want to use database our network, our just keep both and use my DatabaseBoundResource to get the best of both :)

* Start to build stuff :) and run ktlint format from the gradle to improve your code quality before you build

More info here :

https://developer.android.com/topic/libraries/architecture
