package ir.whoisabel.androidCoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import ir.whoisabel.androidCoroutines.databinding.ActivityKotlinCoroutineJobsBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class KotlinCoroutineJobsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKotlinCoroutineJobsBinding

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 // ms
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKotlinCoroutineJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.jobButton.setOnClickListener {
            if (!::job.isInitialized) {
                initJob()
            }
            binding.jobProgressBar.startJobOrCancel(job)
        }

    }

    private fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            println("debug: $job is already active. Cancelling...")
            resetJob()
        } else {
            binding.jobButton.text = context.getString(R.string.label_cancel_job_1)
            CoroutineScope(IO + job).launch {
                println("debug: coroutine $this is activated with job ${job}.")
                for (i in PROGRESS_START..PROGRESS_MAX){
                    delay((JOB_TIME/PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete!")
            }
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
        }
        initJob()
    }

    private fun initJob() {
        binding.jobButton.text = getString(R.string.label_start_job_1)
        updateJobCompleteTextView("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (it.isNullOrBlank()) {
                    msg = "Unknown cancellation error."
                }
                println("debug: $job was cancelled. Reason $msg")
                showToast(msg)
            }
            binding.jobProgressBar.max = PROGRESS_MAX
            binding.jobProgressBar.progress = PROGRESS_START
        }
    }

    private fun updateJobCompleteTextView(text: String) {
        lifecycleScope.launch(Main) {
            binding.jobCompleteText.text = text
        }
    }

    private fun showToast(text: String?) {
        lifecycleScope.launch(Main) {
            Toast.makeText(this@KotlinCoroutineJobsActivity, text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}