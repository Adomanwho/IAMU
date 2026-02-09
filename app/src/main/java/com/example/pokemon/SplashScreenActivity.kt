package com.example.pokemon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.pokemon.api.PokemonWorker
import com.example.pokemon.databinding.ActivitySplashScreenBinding
import com.example.pokemon.framework.applyAnimation
import com.example.pokemon.framework.callDelayed
import com.example.pokemon.framework.getBooleanProperty
import com.example.pokemon.framework.isOnline
import com.example.pokemon.framework.startActivity

private const val DELAY = 3000L
private const val CIRCLE_ANIMATION_DURATION = 1000L
private const val CIRCLE_STAGGER_DELAY = 400L

const val DATA_IMPORTED = "com.example.pokemon.data_imported"
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()
        redirect()
    }

    private fun startAnimations() {
        // Animate circles with staggered timing
        animateCircle(binding.circleYellow, 0L, false)
        animateCircle(binding.circleRed, CIRCLE_STAGGER_DELAY, false)
        animateCircle(binding.circleBlue, CIRCLE_STAGGER_DELAY * 2, true)

        // Show image after all circles have expanded and disappeared
        val imageDelay = (CIRCLE_STAGGER_DELAY * 2) + CIRCLE_ANIMATION_DURATION + 100L
        Handler(Looper.getMainLooper()).postDelayed({
            binding.ivSplash.visibility = View.VISIBLE
            binding.ivSplash.applyAnimation(R.anim.text_appear)
        }, imageDelay)
    }

    private fun animateCircle(circle: View, delay: Long, isLast: Boolean) {
        Handler(Looper.getMainLooper()).postDelayed({
            circle.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, R.anim.color_expand)

            if (isLast) {
                // When the last circle animation ends, hide all circles
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        binding.circleContainer.visibility = View.GONE
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }

            circle.startAnimation(animation)
        }, delay)
    }

    private fun redirect() {
        if(getBooleanProperty(DATA_IMPORTED)){
            callDelayed(DELAY) {startActivity<HostActivity>()}
        } else{
            if(isOnline()){
                WorkManager.getInstance(this).apply {
                    enqueueUniqueWork(
                        DATA_IMPORTED,
                        ExistingWorkPolicy.KEEP,
                        OneTimeWorkRequest.from(PokemonWorker::class.java)
                    )
                }
            } else{
                binding.tvSplash.text = getString(R.string.no_internet)
                callDelayed(DELAY) {finish()}
            }
        }
    }


}


























