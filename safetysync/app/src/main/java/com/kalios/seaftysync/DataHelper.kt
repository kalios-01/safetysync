import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class DataHelper(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    suspend fun saveData(bitmap: Bitmap, username: String) {
        val bitmapString = encodeBitmapToString(bitmap)
        withContext(Dispatchers.IO) {
            editor.putString("bitmap", bitmapString)
            editor.putString("username", username)
            editor.apply()
        }
    }

    suspend fun retrieveBitmap(): Bitmap? {
        return withContext(Dispatchers.IO) {
            val bitmapString = sharedPreferences.getString("bitmap", null)
            bitmapString?.let { decodeStringToBitmap(it) }
        }
    }

    suspend fun retrieveUsername(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString("username", null)
        }
    }

    private fun encodeBitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun decodeStringToBitmap(encodedString: String): Bitmap {
        val decodedByteArray = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
    }
}
