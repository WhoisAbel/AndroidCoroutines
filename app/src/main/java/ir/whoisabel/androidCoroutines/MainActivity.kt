package ir.whoisabel.androidCoroutines

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.whoisabel.androidCoroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val RESULT_1 = "RESULT #1"
    private val RESULT_2 = "RESULT #2"
    private val JOB_TIMEOUT = 2100L

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
                //fakeApiResult()
            }
            //fakeApiResultForWorkingWithLaunch()
            //fakeApiResultForWorkingWithAsync()
            //fakeApiResultForSequential()
        }
        testRunBlocking()

        binding.btnStartOtherActivity.setOnClickListener {
            startActivity(Intent(this, KotlinCoroutineJobsActivity::class.java))
        }
    }

    private fun setNewText(input: String) {
        val newText = binding.text.text.toString() + "\n$input"
        binding.text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    // launch
    private fun fakeApiResultForWorkingWithLaunch() {
        val startTime = System.currentTimeMillis()
        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
                    setTextOnMainThread("Got ${getResult1FromApi()}")
                }
                println("debug: compeleted job1 in $time1 ms.")
            }
            job1.join()

            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name}")
                    setTextOnMainThread("Got ${getResult2FromApi()}")
                }
                println("debug: compeleted job2 in $time2 ms.")
            }
            job2.join()
        }
        parentJob.invokeOnCompletion {
            println("debug: Total time is  ${System.currentTimeMillis() - startTime} ms.")
        }

    }

    // async
    private fun fakeApiResultForWorkingWithAsync() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }

                val result2: Deferred<String> = async {
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult2FromApi()
                }

                setTextOnMainThread("Got ${result1.await()}")
                setTextOnMainThread("Got ${result2.await()}")
            }

            println("debug: Total time is  $executionTime ms.")

        }
    }

    // SEQUENTIAL Background Tasks with Kotlin Coroutines (async and await)
    private fun fakeApiResultForSequential() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {

                //Coroutines will FREEZE your UI
                withContext(Main) {
                    delay(7000)
                    println("debug: Coroutines will FREEZE your UI: ${Thread.currentThread().name}")
                }
                /*
                withContext(Dispatchers.Default) {
                        println("debug: launching job1: ${Thread.currentThread().name}")
                        getResult1FromApi()
                    }
                ===================================
               async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }.await()
                *
                *
                * */

                val result1 =
                    withContext(Dispatchers.Default) {
                        println("debug: launching job1: ${Thread.currentThread().name}")
                        getResult1FromApi()
                    }

                val result3 = withContext(Dispatchers.Default) {
                    println("debug: launching job3: ${Thread.currentThread().name}")
                    println("debug: ${getResult3FromApi(result1)}")
                }

            }
            println("debug: total time is $executionTime")
        }
    }

    //WTF is runBlocking{} in Kotlin Coroutines
    private fun testRunBlocking() {

        // job #1
        CoroutineScope(Main).launch {
            println("debug: Starting jon in thread: ${Thread.currentThread().name}")

            val result1 = getResult()
            println("debug: $result1")

            val result2 = getResult()
            println("debug: $result2")

            val result3 = getResult()
            println("debug: $result3")

            val result4 = getResult()
            println("debug: $result4")

            val result5 = getResult()
            println("debug: $result5")
        }

        // job #2
        CoroutineScope(Main).launch {
           delay(1000)
            runBlocking {
                println("debug: Blocking Thread ${Thread.currentThread().name}")
                delay(4000)
                println("debug: Done blocking Thread ${Thread.currentThread().name}")

            }
        }
    }

    private suspend fun getResult(): Int {
        delay(1000)
        return Random.nextInt(0, 1000)
    }

    private suspend fun fakeApiResult() {
        withContext(IO) {
            val job = withTimeoutOrNull(JOB_TIMEOUT) {
                val result1 = getResult1FromApi()
                println("debug: $result1")
                setTextOnMainThread(result1)

                val result2 = getResult2FromApi()
                println("debug: $result2")
                setTextOnMainThread(result2)
            }

            if (job == null) {
                val cancelMessage = "Cancelling job ... Job took longer than $JOB_TIMEOUT ms"
                println("debug: $cancelMessage")
                setTextOnMainThread(cancelMessage)
            }
        }
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

    private suspend fun getResult3FromApi(result: String): String {
        logThreadName("getResult1FromApi1")
        delay(1000)
        if (result == RESULT_1) {
            return "SEQUENTIAL Background Tasks with Kotlin Coroutines (async and await)"
        }
        throw CancellationException(" Result #1 was incorrect...")
    }

    private fun logThreadName(methodName: String) {
        println("debug $methodName: ${Thread.currentThread().name}")
    }

}


