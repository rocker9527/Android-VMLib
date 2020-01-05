package me.shouheng.sample.view

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import me.shouheng.mvvm.base.CommonActivity
import me.shouheng.mvvm.base.anno.ActivityConfiguration
import me.shouheng.mvvm.base.anno.StatusBarConfiguration
import me.shouheng.mvvm.base.anno.StatusBarMode
import me.shouheng.mvvm.bean.Status
import me.shouheng.sample.R
import me.shouheng.sample.databinding.ActivityMainBinding
import me.shouheng.sample.event.SimpleEvent
import me.shouheng.sample.vm.MainViewModel
import me.shouheng.utils.data.StringUtils
import me.shouheng.utils.ui.BarUtils
import me.shouheng.utils.ui.ToastUtils
import org.greenrobot.eventbus.Subscribe

/**
 * MVVM 框架演示工程
 *
 * @author Wngshhng 2019-6-29
 */
@ActivityConfiguration(
    useEventBus = false,
    layoutResId = R.layout.activity_main,
    statusBarConfiguration = StatusBarConfiguration(
        statusBarMode = StatusBarMode.DARK,
        statusBarColor = Color.BLACK
    )
)
class MainActivity : CommonActivity<ActivityMainBinding, MainViewModel>() {

    override fun doCreateView(savedInstanceState: Bundle?) {
        addSubscriptions()
        initViews()
        vm.startLoad()
        BarUtils.addMarginTopEqualStatusBarHeight(f(R.id.container))
    }

    private fun addSubscriptions() {
        vm.getObservable(String::class.java).observe(this, Observer {
            when(it!!.status) {
                Status.SUCCESS -> { ToastUtils.showShort(it.data) }
                Status.FAILED -> { ToastUtils.showShort(it.message) }
                Status.LOADING -> {/* temp do nothing */ }
                else -> {/* temp do nothing */ }
            }
        })
    }

    private fun initViews() {
        val fragment = MainFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        setSupportActionBar(binding.toolbar)
    }

    @Subscribe
    fun onGetMessage(simpleEvent: SimpleEvent) {
        toast(StringUtils.format(R.string.sample_main_activity_received_msg, javaClass.simpleName, simpleEvent.msg))
    }
}
