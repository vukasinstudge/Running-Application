package rs.ac.bg.etf.running;

import android.view.View;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.login.LoginViewModel;

public class NavDrawerUtil {

    private MainActivity mainActivity;
    private LoginViewModel loginViewModel;

    public NavDrawerUtil(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        loginViewModel = new ViewModelProvider(mainActivity).get(LoginViewModel.class);
    }

    private interface NavHostFragmentChanger {
        NavController change(int id);
    }

    private NavDrawerUtil.NavHostFragmentChanger navHostFragmentChanger;

    public void setup(
            NavigationView navigationView,
            FragmentManager fragmentManager,
            int[] navResourceIds,
            int containerId) {
        Map<Integer, String> navGraphIdToTagMap = new HashMap<>();
        int homeNavGraphId = 0;

        navigationView.setCheckedItem(R.id.nav_graph_login);

        for (int i = 0; i < navResourceIds.length; i++) {
            String tag = "navHostFragment#" + i;

            NavHostFragment navHostFragment = obtainNavHostFragment(
                    fragmentManager,
                    tag,
                    navResourceIds[i],
                    containerId
            );
            int navGraphId = navHostFragment.getNavController().getGraph().getId();

            navGraphIdToTagMap.put(navGraphId, tag);

            if (i == 0) {
                homeNavGraphId = navGraphId;
            }

            if (navigationView.getCheckedItem().getItemId() == navGraphId) {
                attachNavHostFragment(fragmentManager, navHostFragment, i == 0);
            } else {
                detachNavHostFragment(fragmentManager, navHostFragment);
            }
        }

        String homeTag = navGraphIdToTagMap.get(homeNavGraphId);

        AtomicReference<Boolean> isOnHomeWrapper = new AtomicReference<>(
                homeNavGraphId == navigationView.getCheckedItem().getItemId());

        AtomicReference<String> currentTagWrapper = new AtomicReference<>(
                navGraphIdToTagMap.get(navigationView.getCheckedItem().getItemId()));

        navHostFragmentChanger = id -> {
            if (id == R.id.nav_drawer_logout) {
                loginViewModel.updateUser(loginViewModel.getCurrentUser().getValue().getUsername(), false);
                loginViewModel.setCurrentUser(null);
                mainActivity.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                navHostFragmentChanger.change(R.id.nav_graph_login);
            } else {
                if (!fragmentManager.isStateSaved()) {
                    String dstTag = navGraphIdToTagMap.get(id);

                    navigationView.getMenu().findItem(id).setChecked(true);

                    NavHostFragment homeNavHostFragment = (NavHostFragment)
                            fragmentManager.findFragmentByTag(homeTag);
                    NavHostFragment dstNavHostFragment = (NavHostFragment)
                            fragmentManager.findFragmentByTag(dstTag);

                    if (!dstTag.equals(currentTagWrapper.get())) {
                        fragmentManager.popBackStack(homeTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        if (!dstTag.equals(homeTag)) {
                            fragmentManager.beginTransaction()
                                    .detach(homeNavHostFragment)
                                    .attach(dstNavHostFragment)
                                    .setPrimaryNavigationFragment(dstNavHostFragment)
                                    .addToBackStack(homeTag)
                                    .setReorderingAllowed(true)
                                    .commit();
                        }

                        currentTagWrapper.set(dstTag);
                        isOnHomeWrapper.set(dstTag.equals(homeTag));
                    }
                    return dstNavHostFragment.getNavController();
                }
                return null;
            }
            return null;
        };

        loginViewModel.getUserList().observe(mainActivity, users -> {
            boolean someoneLoggedIn = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).isStayLoggedIn()) {
                    someoneLoggedIn = true;
                    loginViewModel.setCurrentUser(users.get(i));
                }
            }

            if (someoneLoggedIn) {
                TextView usernameHeader = ((NavigationView) mainActivity.findViewById(R.id.nav_view)).getHeaderView(0)
                        .findViewById(R.id.username_header);
                TextView emailHeader = ((NavigationView) mainActivity.findViewById(R.id.nav_view)).getHeaderView(0)
                        .findViewById(R.id.email_header);

                usernameHeader.setText(loginViewModel.getCurrentUser().getValue().getUsername());
                emailHeader.setText(loginViewModel.getCurrentUser().getValue().getEmail());

                mainActivity.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                navHostFragmentChanger.change(R.id.nav_graph_routes);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                menuItem -> navHostFragmentChanger.change(menuItem.getItemId()) != null);

        int finalHomeNavGraphId = homeNavGraphId;
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (!isOnHomeWrapper.get() && !isOnBackStack(fragmentManager, homeTag)) {
                navigationView.setCheckedItem(finalHomeNavGraphId);
            }
        });


        /*boolean someoneLoggedIn = false;
        users = mainActivity.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).isStayLoggedIn()) {
                someoneLoggedIn = true;
                loginViewModel.setCurrentUser(users.get(i));
            }
        }

        if (someoneLoggedIn) navHostFragmentChanger.change(R.id.nav_graph_routes);*/
    }

    public NavController changeNavHostFragment(int id) {
        return navHostFragmentChanger.change(id);
    }

    private NavHostFragment obtainNavHostFragment(
            FragmentManager fragmentManager,
            String tag,
            int navResourceId,
            int containerId) {
        NavHostFragment existingNavHostFragment = (NavHostFragment)
                fragmentManager.findFragmentByTag(tag);
        if (existingNavHostFragment != null) {
            return existingNavHostFragment;
        }

        NavHostFragment newNavHostFragment = NavHostFragment.create(navResourceId);
        fragmentManager.beginTransaction()
                .add(containerId, newNavHostFragment, tag)
                .commitNow();
        return newNavHostFragment;
    }

    private void attachNavHostFragment(
            FragmentManager fragmentManager,
            NavHostFragment navHostFragment,
            boolean isPrimary) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.attach(navHostFragment);
        if (isPrimary) {
            fragmentTransaction.setPrimaryNavigationFragment(navHostFragment);
        }
        fragmentTransaction.commitNow();
    }

    private void detachNavHostFragment(
            FragmentManager fragmentManager,
            NavHostFragment navHostFragment) {
        fragmentManager.beginTransaction()
                .detach(navHostFragment)
                .commitNow();
    }

    private boolean isOnBackStack(
            FragmentManager fragmentManager,
            String backStackEntryName) {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            if (fragmentManager.getBackStackEntryAt(i).getName().equals(backStackEntryName)) {
                return true;
            }
        }
        return false;
    }

}
