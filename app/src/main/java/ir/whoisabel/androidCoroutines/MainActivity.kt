package ir.whoisabel.androidCoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ir.whoisabel.androidCoroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val RESULT_1 = "RESULT #1"
    private val RESULT_2 = "RESULT #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

        binding.button.setOnClickListener {
            // IO, Main, Default
            CoroutineScope(IO).launch {
                fakeApiResult()
            }
        }
    }

    private fun setNewText(input: String) {
        val newText = binding.text.text.toString() + "\n$input"
        binding.text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String){
        withContext(Main){
            setNewText(input)
        }
    }

    private suspend fun fakeApiResult() {
        val result1 = getResult1FromApi()
        println("debug: $result1")
        setTextOnMainThread(result1)

        val result2 = getResult2FromApi()
        println("debug: $result2")
        setTextOnMainThread(result2)
    }

    private suspend fun getResult1FromApi(): String {
        logThreadName("getResult1FromApi1")
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResult2FromApi(): String {
        logThreadName("getResult1FromApi2")
        delay(1000)
        return RESULT_2
    }

    private fun logThreadName(methodName: String) {
        println("debug $methodName: ${Thread.currentThread().name}")
    }

}


