# DSList

 [![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

 DSList is a paginated list builder library for Android:
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
 	<version>0.5.0</version>
 	<type>pom</type>
 </dependency>
 ```

 ### Gradle

 Add dependencies:

 ```groovy
 dependencies {
    implementation 'thevoid.whichbinds.dslist:dslist:0.2.0'
 }
 ```

 Make sure that you have either `jcenter()` or `mavenCentral()` in the list of repositories:

 ```
 repository {
     jcenter()
 }
 ```

 ## Samples
 
 [RedditDSList]( https://github.com/pedrosr7/RedditDSList) is an example app where we can see different ways to implement a list using the DSList library

 ## Quick Start
 
  ```kotlin
listDSL<String, RedditPost> {
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
            ul {
               for (value in listOfRedditPost) {
                li {
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
}
 ```
 
 ## Documentation
 
 To build a simple or paged list you can use different functions or properties.
 
 ### properties
 
  - **recyclerView**: Instance of RecyclerView class.
  - **ul**: List of Ul type.
  - **li**: List item of Row type.
  - **after**: Last id of the row list.
  - **before**: First id of the row list.

 ### base
 
 First we need to provide the type arguments <`Key`,`Model`>.
 
 - **Key**: Id that is associated to each row. It can be of any type (String, Int, etc)
 - **Model**: Type of data that we want to show in the list, for example an String or data class
 
 ```kotlin
 data class RedditPost (
    val key: String,
    val title: String,
    val author: String,
 )
 ```

 ```kotlin
 listDSL<String, RedditPost> {}
 ```

 Also we need the reference to the RecyclerView visual component.
 
 ```kotlin
 listDSL<String, RedditPost> {
    recyclerView = this@MainActivity.recyclerView
 }
 ```

 And finally we can use the different functions to build our list
 
 Important: Don't forget to add the layout manager.
 
 ```kotlin
 recyclerView.layoutManager = LinearLayoutManager(this) // or GridLayoutManager(this,2)
 ```
 ### ul{}
 
 The ul function is a lambda with reciver that allows you to add `li` items to the list.

 ### li{}
 
 The li function allows you to add the content of the list and associate it with the visual components that have been defined.
 You have four properties `id`, `content`, `viewType` and `viewBind`.
 
 - **id**: Row identifier (optional)
 - **content**: Row content (optional)
 - **viewType**: Id of the view already designed for the items (required)
 - **viewBind**: Function that associates content with visual components (optional)
 
 ```kotlin
 listDSL<String, RedditPost> {
    recyclerView = this@MainActivity.recyclerView
   
    ul {
        for (value in listOfRedditPost) {
        li {
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
 listDSL<String, RedditPost> {
    recyclerView = this@MainActivity.recyclerView
     
    observe(redditPostLiveData) { posts ->
        posts?.let {
            ul {
                for (value in listOfRedditPost) {
                li {
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
 }
 ```

 ### load{}
  
  The load is a reactive function that is call every time we can not scroll further.
  - **REFRESH**: Called when data is update.
  - **PREPEND**: Called when reaches the top of the list.
  - **APPEND**: Called when reaches the bottom of the list.
  
 ```kotlin
 listDSL<String, RedditPost> {
    recyclerView = this@MainActivity.recyclerView
      
    load {
        when(it) {
            ListState.PREPEND -> mainViewModel.getPosts(after = null, before = before)
            ListState.APPEND -> mainViewModel.getPosts(after = after, before = null)
        }
    }

    observe(redditPostLiveData) { posts ->
        posts?.let {
            ul {
                for (value in listOfRedditPost) {
                li {
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
 }
 ```

 ## R8 and ProGuard

 DSList is fully compatible with R8 out of the box and doesn't require adding any extra rules.
 
 ## Requirements

 * Compile SDK: 29+
 * Min SDK: 23+

 ## Author
 
    Pedro Sánchez Ramírez
  
 ## License

    Copyright (c) 2020, DSList Contributors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
