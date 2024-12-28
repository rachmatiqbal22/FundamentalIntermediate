        package com.dicoding.picodiploma.fundamentalintermediate.data.response

        import android.util.Log
        import androidx.paging.PagingSource
        import androidx.paging.PagingState
        import com.dicoding.picodiploma.fundamentalintermediate.data.retrofit.ApiService

        class StoryPagingSource(
            private val apiService: ApiService,
            private val token: String
        ) : PagingSource<Int, StoryItem>() {

            private companion object {
                const val INITIAL_PAGE_INDEX = 1
            }

            override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    val anchorPage = state.closestPageToPosition(anchorPosition)
                    anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
                return try {
                    val page = params.key ?: INITIAL_PAGE_INDEX

                    val response = apiService.getStories("Bearer $token", page, params.loadSize)

                    if (response.isSuccessful) {
                        val storyResponse = response.body()
                        if (storyResponse == null || storyResponse.listStory.isEmpty()) {
                            Log.d("StoryPagingSource", "No data available for page: $page")
                            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
                        }
                        LoadResult.Page(
                            data = storyResponse.listStory,
                            prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                            nextKey = if (storyResponse.listStory.isEmpty()) null else page + 1
                        )
                    } else {
                        LoadResult.Error(Exception("Error: ${response.message()}"))
                    }
                } catch (exception: Exception) {
                    Log.e("StoryPagingSource", "Load error: $exception")
                    LoadResult.Error(exception)
                }
            }
        }
