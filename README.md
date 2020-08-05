# DSList

 [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

 DSList is a paginated lists builder library for Android:
 * Type-safe builder: DSList is base on domain-specific language (DSL), suitable for building complex hierarchical data structures in a semi-declarative way.
 * Reactive: DSList use StateFlow to handle the state of the list. This helps you load and display small chunks of data at a time.

 ## Using in your projects

 The libraries are published to [DSList](https://bintray.com/pedrosr7/DSList/thevoid.whichbinds.dslist) bintray repository,
 linked to [JCenter](https://bintray.com/bintray/jcenter?filterByPkgName=thevoid.whichbinds.dslist).
 
 ### Maven

 Add dependencies:

 ```xml
 <dependency>
 	<groupId>thevoid.whichbinds.dslist</groupId>
 	<artifactId>dslist</artifactId>
 	<version>0.1.1</version>
 	<type>pom</type>
 </dependency>
 ```

 ### Gradle

 Add dependencies:

 ```groovy
 dependencies {
     implementation 'thevoid.whichbinds.dslist:dslist:0.1.1'
 }
 ```

 Make sure that you have either `jcenter()` or `mavenCentral()` in the list of repositories:

 ```
 repository {
     jcenter()
 }
 ```

 #### R8 and ProGuard

 DSList is fully compatible with R8 out of the box and doesn't require adding any extra rules.
 
 ## Documentation
 
 To build a simple or paged list you can use different functions or properties.
 First we need to create data model for the list.

 ```kotlin
    data class RedditPost (
         val key: String,
         val title: String,
         val author: String,
     )
  ```

 We need to provide the type arguments `Key` and `Model`.
 
 ```kotlin
    listPaged<String, RedditPost> {}
 ```

 Also we need the reference to the RecyclerView visual component.
 
 ```kotlin
    listPaged<String, RedditPost> {
       recyclerView = this@MainActivity.recyclerView
    }
 ```

 And finally we can use the different functions to build our list
 
 Note: No olvides añadir el layaout manager al RecyclerView
 
 ```kotlin
    recyclerView.layoutManager = LinearLayoutManager(this) // or GridLayoutManager(this,2)
 ```

 ### row{}
 
 The row function allows you to add the content of the list and associate it with the visual components that have been defined.
 You have four properties `id`, `content`, `viewType` and `viewBind`.
 
 * id: Row identifier (optional)
 * content: Row content (optional)
 * viewType: Id of the view already designed for the items (required)
 * viewBind: Function that associates content with visual components (required)
 
 ```kotlin
   listPaged<String, RedditPost> {
     recyclerView = this@MainActivity.recyclerView
   
     for (value in listOfRedditPost) {
       row {
         id = key
         content = value
         viewType = R.layout.item_reddit_post
         viewBind { content, itemView ->
               val title: TextView? =
                   itemView.findViewById(R.id.textView_title)
               val author: TextView? =
                   itemView.findViewById(R.id.textView_author)
               title?.text = content.title
               author?.text = content.author
         }
       }
     }
   }
   ```

 ### observe{}
 
 The observe function allows you to update the items of the list, 
 using for this the LiveData class that allows us to create an observable data holder.
 
 ```kotlin
   val redditPostLiveData: MutableLiveData<List<RedditPost>> by lazy {
         MutableLiveData<List<RedditPost>>()
   }
  ```
 ```kotlin
   listPaged<String, RedditPost> {
     recyclerView = this@MainActivity.recyclerView
     
     observe(redditPostLiveData) { posts ->
       posts?.let {
         for (value in listOfRedditPost) {
           row {
             id = key
             content = value
             viewType = R.layout.item_reddit_post
             viewBind { content, itemView ->
               val title: TextView? =
                   itemView.findViewById(R.id.textView_title)
               val author: TextView? =
                   itemView.findViewById(R.id.textView_author)
               title?.text = content.title
               author?.text = content.author
             }
           }
         }           
       }
     }
   }
   ```
 ### load{}
  
  The load is a reactive function that is call every time we can not scroll further.
  * ListState.REFRESH: Called when data is update.
  * ListState.PREPEND: Called when reaches the top.
  * ListState.APPEND: Called when reaches the bottom.
  
  ```kotlin
    listPaged<String, RedditPost> {
      recyclerView = this@MainActivity.recyclerView
      
      load {
        when(it) {
          ListState.REFRESH -> print("")
          ListState.PREPEND -> mainViewModel.getPosts(after = null, before = before)
          ListState.APPEND -> mainViewModel.getPosts(after = after, before = null)
        }
      }
      observe(redditPostLiveData) { posts ->
        posts?.let {
          for (value in listOfRedditPost) {
            row {
              id = key
              content = value
              viewType = R.layout.item_reddit_post
              viewBind { content, itemView ->
                val title: TextView? =
                    itemView.findViewById(R.id.textView_title)
                val author: TextView? =
                    itemView.findViewById(R.id.textView_author)
                title?.text = content.title
                author?.text = content.author
              }
            }
          }           
        }
      }
    }
  ```

 ## Building

 This library is built with Gradle. To build it, use `./gradlew build`.
 You can import this project into IDEA, but you have to delegate build actions
 to Gradle (in Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Runner)

 ### Requirements

 * JDK >= 11 referred to by the `JAVA_HOME` environment variable.
 * JDK 1.6 referred to by the `JDK_16` environment variable. It is okay to have `JDK_16` pointing to `JAVA_HOME` for external contributions.
 * JDK 1.8 referred to by the `JDK_18` environment variable. Only used by nightly stress-tests. It is okay to have `JDK_18` pointing to `JAVA_HOME` for external contributions.

## License

    Copyright 2020 DSList

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
