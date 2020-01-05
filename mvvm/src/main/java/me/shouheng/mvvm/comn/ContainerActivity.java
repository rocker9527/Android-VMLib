package me.shouheng.mvvm.comn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.shouheng.mvvm.R;
import me.shouheng.mvvm.base.CommonActivity;
import me.shouheng.mvvm.databinding.MvvmsActivityContainerBinding;
import me.shouheng.utils.app.ActivityUtils;
import me.shouheng.utils.stability.L;

/**
 * The common container activity for ONE SHOT fragment:
 * Call {@link #open(Class)} or {@link #openFragment(Class)} method
 * directly to get a builder for container.
 * And then set arguments by builder for fragment.
 *
 * Example:
 * <blockquote><pre>
 * ContainerActivity.open(SampleFragment::class.java)
 *     // set arguments for the fragment
 *     .put(SampleFragment.ARGS_KEY_TEXT, "Here is the text from the arguments.")
 *     // launch the container activity
 *     .launch(context!!)
 * </pre>
 * </blockquote>
 *
 * @author <a href="mailto:shouheng2015@gmail.com">WngShhng</a>
 */
public class ContainerActivity extends CommonActivity<MvvmsActivityContainerBinding, EmptyViewModel> {

    /**
     * Global command handler.
     */
    private static List<CommandHandler> commandHandlers = new CopyOnWriteArrayList<>();

    /**
     * Key for the {@link Fragment} class used to create the fragment instance.
     */
    public static final String KEY_EXTRA_FRAGMENT_CLASS = "__extra_key_fragment_class";

    /**
     * Key for the command.
     */
    public static final String KEY_EXTRA_COMMAND        = "__extra_key_command";

    /**
     * Get a {@link ActivityUtils.Builder} object to build a request for container activity.
     * You can set argument of your fragment by the methods provided by the builder directly.
     *
     * NOTE : THE BUNDLE FROM ACTIVITY INTENT WILL BE USED AS THE ARGUMENTS OF FRAGMENT.
     *
     * @param fragment  the fragment class
     * @param <F>       the fragment class type
     * @return          the activity builder object
     * @see #openFragment(Class) for {@link android.app.Fragment}
     */
    public static <F extends Fragment> ActivityUtils.Builder<ContainerActivity> open(Class<F> fragment) {
        return ActivityUtils.open(ContainerActivity.class).put(KEY_EXTRA_FRAGMENT_CLASS, fragment);
    }

    /**
     * Launch current activity with given command. The command will be handled by the
     * already registered {@link CommandHandler} by {@link #registerCommandHandler(CommandHandler)}.
     *
     * @param command the command
     */
    public static ActivityUtils.Builder<ContainerActivity> open(int command) {
        return ActivityUtils.open(ContainerActivity.class).put(KEY_EXTRA_COMMAND, command);
    }

    /**
     * Get a {@link ActivityUtils.Builder} for {@link android.app.Fragment}.
     *
     * NOTE : THE BUNDLE FROM ACTIVITY INTENT WILL BE USED AS THE ARGUMENTS OF FRAGMENT.
     *
     * @param fragment fragment
     * @param <F>      fragment type
     * @return         the builder
     * @see #open(Class) for {@link Fragment}
     */
    public static <F extends android.app.Fragment> ActivityUtils.Builder<ContainerActivity> openFragment(Class<F> fragment) {
        return ActivityUtils.open(ContainerActivity.class).put(KEY_EXTRA_FRAGMENT_CLASS, fragment);
    }

    /**
     * Register a global command handler.
     *
     * @param handler the command handler.
     */
    public static void registerCommandHandler(CommandHandler handler) {
        if (!commandHandlers.contains(handler)) {
            commandHandlers.add(handler);
        }
    }

    /**
     * Unregister a global command handler
     *
     * @param handler the command handler
     */
    public static void unRegisterCommandHandler(CommandHandler handler) {
        commandHandlers.remove(handler);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.mvvms_activity_container;
    }

    @Override
    protected void doCreateView(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        Class fragmentClass = (Class) intent.getSerializableExtra(KEY_EXTRA_FRAGMENT_CLASS);
        int command = intent.getIntExtra(KEY_EXTRA_COMMAND, Integer.MIN_VALUE);
        // the bundle from activity intent will be used as the arguments of fragment
        Bundle bundle = intent.getExtras();
        try {
            if (fragmentClass == null && command == Integer.MIN_VALUE) {
                L.e("Error : fragmentClass is null.");
                return;
            }
            if (fragmentClass != null) {
                if (Fragment.class.isAssignableFrom(fragmentClass)) {
                    Fragment fragment = (Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment)
                            .commit();
                } else if (android.app.Fragment.class.isAssignableFrom(fragmentClass)) {
                    android.app.Fragment fragment = (android.app.Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment)
                            .commit();
                }
            } else {
                // handle command by command handler
                for (CommandHandler handler : commandHandlers) {
                    handler.handle(this, command, R.id.container, bundle);
                }
            }
        } catch (InstantiationException e) {
            L.d(e);
        } catch (IllegalAccessException e) {
            L.d(e);
        }
    }

    /**
     * Command handler callback
     */
    public interface CommandHandler {

        /**
         * Method to handle the command.
         *
         * NOTE: THE COMMAND WITH VALUE {@link Integer#MIN_VALUE} WILL BE IGNORED!
         *
         * @param activity          the container activity
         * @param intentExtras      the extras from {@link Activity#getIntent()}
         * @param containerLayoutId container layout id
         * @param command           command, you can use any integer except {@link Integer#MIN_VALUE}.
         */
        void handle(ContainerActivity activity, int command, @IdRes int containerLayoutId, Bundle intentExtras);
    }
}
