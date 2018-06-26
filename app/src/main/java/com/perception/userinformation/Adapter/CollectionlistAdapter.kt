package com.perception.userinformation.Adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.perception.userinformation.R
import android.view.View
import android.widget.*
import com.perception.userinformation.https_server_calling.PicassoTrustAll
import com.perception.userinformation.Model.UserModel
import android.widget.LinearLayout


class CollectionlistAdapter( val context: Context,val cellheight:Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var TAG: String = "CollectionlistAdapter";
    var items: ArrayList<UserModel> = ArrayList()
    internal var retryPageLoad: Boolean = false;
    internal var isLoadingAdded: Boolean = false;
    private val ITEM = 0
    private val LOADING = 1





    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        Log.d(TAG," onBindViewHolder item count " + position);

        val getitem = getItemViewType(position)



        when (getitem) {


            ITEM -> {

                var currentscreen = holder as MYViewHolder
                currentscreen.bindItems(items.get(position), context,cellheight);
            }

            LOADING -> {
                var Loadinscreen = holder as LoadingVH

            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        // val v = LayoutInflater.from(context).inflate(R.layout.widget_listusers, parent, false)
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            ITEM -> {
                val viewItem = LayoutInflater.from(context).inflate(R.layout.widget_listusers, parent, false)
                viewHolder = MYViewHolder(viewItem)
            }
            LOADING -> {
                val viewLoading = LayoutInflater.from(context).inflate(R.layout.progressloading, parent, false)
                viewHolder = LoadingVH(viewLoading)
            }
        }
        /*if(viewHolder!=null)
        {
            val fontChanger = FontChangeCrawler(textviewfont)
            fontChanger.replaceFonts(viewHolder as ViewGroup)
        }*/
        return viewHolder!!;
    }

    override fun getItemViewType(position: Int): Int {


        return if (position == items.size - 1 && isLoadingAdded) LOADING else ITEM

    }


    override fun getItemCount(): Int {
        Log.d(TAG," Adapter item count " + items.size);
        return items.size;
    }


    class MYViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var TAG: String = "CollectionlistAdapter";
        fun bindItems(user: UserModel, context: Context,cellheight:Int) {
            var welcometextfont: Typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.ttf")
            var textviewfont: Typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Regular.ttf")
            var ll_lsitContainer: LinearLayout = itemView.findViewById(R.id.ll_lsitContainer);
            var txtv_userfirstname: TextView = itemView.findViewById(R.id.txtv_userfirstname);
            var txtv_userlastnamename: TextView = itemView.findViewById(R.id.txtv_userlastnamename);
            var txtv_welcometext: TextView = itemView.findViewById(R.id.txtv_welcometext);
            var iv_userimage: ImageView = itemView.findViewById(R.id.iv_userimage);

            txtv_welcometext.setTypeface(welcometextfont);
            txtv_userfirstname.setTypeface(textviewfont);
            txtv_userlastnamename.setTypeface(textviewfont);

            txtv_userfirstname.text = user.FirstName + "";
            txtv_userlastnamename.text = "" + user.LastName;
            txtv_welcometext.text = "Welcome, Mr "+user.FirstName + " "+ user.LastName;


          PicassoTrustAll.with(context)!!.load(user.Avatarurl).placeholder(R.drawable.image_placeholder).into(iv_userimage);

          var  continerheight : Int =cellheight / 3;

            val lp : LinearLayout.LayoutParams = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,continerheight)
         ll_lsitContainer.setLayoutParams(lp);
        }
    }

    protected inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mProgressBar: ProgressBar? = null

    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        add(UserModel())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = items.size - 1
        val result = getItem(position)

        if (result != null) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun add(r: UserModel) {

        items.add(r)

        notifyItemInserted(items.size - 1)

    }

    fun addAll(moveResults: ArrayList<UserModel>, size: Int) {
        /// Log.d("FOr data",size+">>>>>>>>>>>>>>>>moveingresult ");


        Log.d(TAG," Adapter item add all count "+moveResults.size);





      for (i in 0..moveResults.size - 1)
        {
            Log.d(TAG," Adapter item add all in for loop count "+moveResults.size +" i val = "+i);
            items.add(moveResults.get(i))

            notifyItemInserted(items.size - 1)
        }


    }


    fun getItem(position: Int): UserModel {
        return items.get(position)
    }



}