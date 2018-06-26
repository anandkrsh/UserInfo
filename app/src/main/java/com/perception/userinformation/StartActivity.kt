package com.perception.userinformation

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log


import com.perception.userinformation.Adapter.CollectionlistAdapter
import com.perception.userinformation.ApiCalling.WebAPIInterface
import com.perception.userinformation.Utill.Constants

import com.perception.userinformation.https_server_calling.Create_trustmanager
import com.perception.userinformation.Model.UserModel
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.support.v7.widget.DefaultItemAnimator
import android.util.DisplayMetrics



class StartActivity : Activity() {

    lateinit var usermodel: UserModel
    var TAG: String = "StartActivity";
    var usercollections: ArrayList<UserModel> = ArrayList()
    lateinit var context: Context;
    val mActivity: Activity = this;
    val layoutManagaer: LinearLayoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
    var itempostion : Int =0;
    private var PAGE_START = 1
    internal var isLastPage: Boolean = false
var perpage_itemcout :Int =0;
    var isloding: Boolean = false;
    private var TOTAL_PAGES = 0
    private var currentPage = 1
    lateinit var adapter: CollectionlistAdapter;
    var  displayMetrics : DisplayMetrics =  DisplayMetrics();


    var BASE_URL : String = "https://reqres.in/"
    var USER_DETAILS : String = "api/users"
    var USER_DETAILS_PAGEWISE : String = "api/users?page: String ="

    //Field Names

    var USER_FIRSTNAME : String = "first_name"
    var USER_LASTNAME : String = "last_name"
    var USER_IMAGEURL : String = "avatar"
    var USER_ID : String = "id"
    var TOTAL_PAGE_FIElD : String = "total_pages"
    var PER_PAGEITEMCOUNT : String = "per_page"
    var CURRENT_PAGE : String = "page"
    val ERROR_MESSAGE_NO_INTERNET: CharSequence = "Please check your network connection!"
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondactivity)
        //getting recyclerview from xml
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        var rv_listitem: RecyclerView
        rv_listitem = findViewById(R.id.rv_listitem)
        rv_listitem.layoutManager = layoutManagaer;
        rv_listitem.setItemAnimator(DefaultItemAnimator())


        adapter = CollectionlistAdapter( mActivity,displayMetrics.heightPixels);
        rv_listitem.adapter = adapter;



        rv_listitem.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.e("RecyclerView", "onScrollStateChanged")
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)



                val visibleItemCount = layoutManagaer.childCount
                val totalItemCount = layoutManagaer.itemCount
                val firstVisibleItemPosition = layoutManagaer.findFirstVisibleItemPosition()
                itempostion = visibleItemCount + firstVisibleItemPosition



                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>visibleItemCount >>>>>>>>>>>>>" + BASE_URL)
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>firstVisibleItemPosition >>>>>>>>>>>>>" + firstVisibleItemPosition)
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>totalItemCount >>>>>>>>>>>>>" + totalItemCount)
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>itempostion >>>>>>>>>>>>>" + itempostion)
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>isLastPage >>>>>>>>>>>>>" + isLastPage)
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>isloding >>>>>>>>>>>>>" + isloding)

                if (!isloding && !isLastPage) {
                    if (itempostion >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isloding = true

                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>LOad more items Page>>>>>>>>>>>>>")

                        Handler().postDelayed({
                            loadnextpage()
                        }, 1000)
                    }
                }

            }
        })







    }


    public override fun onResume() {
        super.onResume()
        if(currentPage>1&&currentPage<TOTAL_PAGES)
            currentPage+=1
        FetchUserDetails(""+currentPage);
    }

    fun FetchUserDetails(pagecount: String) {
        try {
Log.d(TAG," Fetche user Details pagecount "+pagecount)

            /*if(!isConnectedToInternet(mActivity))
            {
                showNoNetworkDialog(mActivity);
                return;
            }*/

            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(Create_trustmanager.unsafeOkHttpClient)
                    .build()


            val apiinterface = retrofit.create(WebAPIInterface::class.java)
            val getrightswipecall = apiinterface.UserDetials(pagecount, "application/json")
            getrightswipecall.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(code: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                    try {
                        val result: String

                        if (response.isSuccessful) {

                            result = response.body().string().toString();

                            if (pagecount.equals("1")) {
                                //Log.d(TAG, ">>>>>>>>>>>>>>>>>>>enter into fill list screen >>>>>>>>>>>>>" + filllist(result))
                                filllist(result.toString());
                            } else {
                                //Log.d(TAG, ">>>>>>>>>>>>>>>>>>>enter into fill next screen >>>>>>>>>>>>>" + fillnext(result))
                                fillnext(result.toString());

                            }
                        } else {

                            result = response.errorBody().string().toString().trim { it <= ' ' }

                        }

                        Log.d(TAG, result.toString() + " jsonvalues values ");
                    } catch (e: Exception) {
                        e.printStackTrace();
                        Log.d(TAG, " setrightswipeapi Error >> " + e.message)
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    Log.d(TAG, "autome onResponse: " + call.toString() + "   " + t.localizedMessage + "   " + t.message)


                }
            })

        } catch (e: Exception) {

            Log.e("Retrofit Error", "setSignin: " + e.localizedMessage)

        }


    }


    fun loadfirstpage() {

        FetchUserDetails("1");
    }

    public fun loadnextpage() {
    currentPage += 1
        FetchUserDetails("" + currentPage);
    }


    public fun filllist(result: String) {

        Log.d(TAG, result.toString() + " jsonvalues values filllist ");
        var response: JSONObject = JSONObject(result);
        var userArray: JSONArray = response.getJSONArray("data")
        currentPage= response.getInt("" + CURRENT_PAGE)
        perpage_itemcout = response.getInt("" + PER_PAGEITEMCOUNT)
        TOTAL_PAGES = response.getInt("" + TOTAL_PAGE_FIElD)
        if (userArray != null && userArray.length() > 0) {
            if (usercollections == null) {
                usercollections = ArrayList();
            } else {
                usercollections.clear();
            }

            for (i in 0 until userArray.length()) {
                usermodel = UserModel();
                var userobj: JSONObject = JSONObject(userArray.getString(i));
                usermodel.FirstName = userobj.getString("" + USER_FIRSTNAME);
                usermodel.LastName = userobj.getString("" + USER_LASTNAME);
                usermodel.Avatarurl = userobj.getString("" + USER_IMAGEURL);
                usermodel.userid = userobj.getString("" + USER_ID);
                usercollections.add(usermodel)
            }





           adapter.addAll(usercollections, usercollections.size)

            Log.d(TAG," Collection Count and per page item count "+perpage_itemcout+" "+ usercollections.size+" "+adapter.getItemCount());
         /* if (currentPage <= TOTAL_PAGES) {
                if (adapter.getItemCount() > perpage_itemcout)
                {
                    adapter.addLoadingFooter()
                } else {
                    isLastPage = true
                }

            }*/


            if (currentPage <= TOTAL_PAGES) {
                adapter.addLoadingFooter()
            }
            else {
             //   isLastPage = true
            }


        }
    }

    public fun fillnext(result: String) {

        Log.d(TAG, result.toString() + " jsonvalues values  fillnext");
        var response: JSONObject = JSONObject(result);
        var userArray: JSONArray = response.getJSONArray("data")
        currentPage= response.getInt("" + CURRENT_PAGE)
        perpage_itemcout = response.getInt("" + PER_PAGEITEMCOUNT)
        TOTAL_PAGES = response.getInt("" + TOTAL_PAGE_FIElD)
        if (userArray != null && userArray.length() > 0) {
            if (usercollections == null) {
                usercollections = ArrayList();
            } else {
                usercollections.clear();
            }

            for (i in 0 until userArray.length()) {
                usermodel = UserModel();
                var userobj: JSONObject = JSONObject(userArray.getString(i));
                usermodel.FirstName = userobj.getString("" + USER_FIRSTNAME);
                usermodel.LastName = userobj.getString("" + USER_LASTNAME);
                usermodel.Avatarurl = userobj.getString("" + USER_IMAGEURL);
                usermodel.userid = userobj.getString("" + USER_ID);
                usercollections.add(usermodel)
            }

        }

        if (usercollections != null && usercollections.size > 0)
        {
            Log.d(TAG, ">>>>>>>>>>>>>>List size>>>>>>>>>")
            adapter.removeLoadingFooter()
            isloding = false

            adapter.addAll(usercollections, usercollections.size)


            if (currentPage != TOTAL_PAGES) {
                if (adapter.getItemCount() > 5)
                    adapter.addLoadingFooter()
            } else {
                isLastPage = true
            }

        }
    }

    fun isConnectedToInternet(_context: Context): Boolean {
        try {
            val connectivity = _context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null)
                    for (i in info.indices)
                        if (info[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                        }

            }
        } catch (e: Exception) {

        }

        return false
    }

    fun showNoNetworkDialog(context: Context): AlertDialog.Builder {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog
                .setMessage(ERROR_MESSAGE_NO_INTERNET)
                .setCancelable(false)
                .setPositiveButton("Settings",
                        DialogInterface.OnClickListener { dialog, id ->
                            // if this button is clicked, close
                            (context as Activity)
                                    .startActivity(Intent(
                                            Settings.ACTION_SETTINGS))
                        })
                .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

        // show it
        alertDialog.show()

        return alertDialog

    }

}
