package hicCups.p.forNotification

import hicCups.p.forNotification.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationClient {

    fun getService(): NotificationService{
        return  Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(NotificationService::class.java)
    }


}
