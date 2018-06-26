package com.perception.userinformation.https_server_calling

import android.content.Context
import android.util.Log

import com.squareup.okhttp.OkHttpClient
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager



class PicassoTrustAll private constructor(context: Context) {

    init {
        val client = OkHttpClient()
        client.hostnameVerifier = HostnameVerifier { s, sslSession -> true }
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(java.security.cert.CertificateException::class)
            override fun checkClientTrusted(
                    x509Certificates: Array<java.security.cert.X509Certificate>,
                    s: String) {
            }

            @Throws(java.security.cert.CertificateException::class)
            override fun checkServerTrusted(
                    x509Certificates: Array<java.security.cert.X509Certificate>,
                    s: String) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, java.security.SecureRandom())
            client.sslSocketFactory = sc.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mInstance = Picasso.Builder(context)
                .downloader(OkHttpDownloader(client))
                .listener { picasso, uri, exception -> Log.e("PICASSO", exception.toString()) }.build()

    }

    companion object {

        private var mInstance: Picasso? = null


        fun with(context: Context): Picasso? {

            if (mInstance == null) {
                PicassoTrustAll(context)
            }
            return mInstance
        }
    }

}
