/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.qi.tt.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class Permissions {

    static final String TAG = Permissions.class.getSimpleName();
    static final Object TRIGGER = new Object();

    @VisibleForTesting
    Lazy<PermissionsFragment> mPermissionsFragment;

    public Permissions(@NonNull final FragmentActivity activity) {
        mPermissionsFragment = getLazySingleton(activity.getSupportFragmentManager());
    }

    public Permissions(@NonNull final Fragment fragment) {
        mPermissionsFragment = getLazySingleton(fragment.getChildFragmentManager());
    }

    @NonNull
    private Lazy<PermissionsFragment> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new Lazy<PermissionsFragment>() {

            private PermissionsFragment permissionsFragment;

            @Override
            public synchronized PermissionsFragment get() {
                if (permissionsFragment == null) {
                    permissionsFragment = getPermissionsFragment(fragmentManager);
                }
                return permissionsFragment;
            }

        };
    }

    private PermissionsFragment getPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        PermissionsFragment rxPermissionsFragment = findPermissionsFragment(fragmentManager);
        boolean isNewInstance = rxPermissionsFragment == null;
        if (isNewInstance) {
            rxPermissionsFragment = new PermissionsFragment();
            fragmentManager
                    .beginTransaction()
                    .add(rxPermissionsFragment, TAG)
                    .commitNow();
        }
        return rxPermissionsFragment;
    }

    private PermissionsFragment findPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        return (PermissionsFragment) fragmentManager.findFragmentByTag(TAG);
    }

    public void setLogging(boolean logging) {
        mPermissionsFragment.get().setLogging(logging);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public Permissions requestEach(FragmentActivity activity, PermissionsFragment.PermissionCallBack callBack, final String... permissions) {
        if (callBack!=null){
            setCallBack(callBack);
        }
//        // 检测是否有  1、未获取到2、且不再提示 的权限，如果有  就  不再 请求权限 ,由于 第一次检测是否 弹出 询问 为  false 在此  无法使用
//        if (!shouldShowRequestPermissionRationaleImplementation(activity,permissions)){
//            mPermissionsFragment.get().onRequestPermissionsResult(new Permission("permission",false,false));
//                return this;
//        }

        List<Permission> list = new ArrayList<>(permissions.length);
        List<String> unrequestedPermissions = new ArrayList<>();

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (String permission : permissions) {
            mPermissionsFragment.get().log("Requesting permission " + permission);
            if (isGranted(permission)) { // 是否 已获取到权限
                // Already granted, or not Android M
                // Return a granted Permission object.
                list.add(new Permission(permission, true, false));
                continue;
            }else  if (isRevoked(permission)) {//  权限  是否已取消
                // Revoked by a policy, return a denied Permission object.
                list.add(new Permission(permission, false, false));
                continue;
            } else { //未请求  到权限
                unrequestedPermissions.add(permission);
            }
        }

        if (!unrequestedPermissions.isEmpty()) {//申请 未获取到的权限
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);
            requestPermissionsFromFragment(unrequestedPermissionsArray);
        }else { // 权限 已 全部获取到
            mPermissionsFragment.get().onRequestPermissionsResult(new Permission("permission",true,false));

        }
        return  this;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationaleImplementation(final Activity activity, final String... permissions) {
        for (String p : permissions) {
            if (!isGranted(p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions) {
        mPermissionsFragment.get().log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions));
        mPermissionsFragment.get().requestPermissions(permissions);
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isGranted(String permission) {
        return !isMarshmallow() || mPermissionsFragment.get().isGranted(permission);
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    private boolean isRevoked(String permission) {
        return isMarshmallow() && mPermissionsFragment.get().isRevoked(permission);
    }

    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

//    private void onRequestPermissionsResult(String permissions[], int[] grantResults) {
//        mPermissionsFragment.get().onRequestPermissionsResult(permissions, grantResults, new boolean[permissions.length]);
//    }

    @FunctionalInterface
    public interface Lazy<V> {
        V get();
    }

    /**
     *  设置 回调 函数
     * @param callBack
     */
    public  void setCallBack(PermissionsFragment.PermissionCallBack callBack){
        mPermissionsFragment.get().setCallBack(callBack);
    }


//    public  void setCallBack(new ){}
//    abstract  class PermissionCallBack{
//        private PermissionsFragment.FragmentPermissionResultCallBack callBack;
//        public  void accept();
//        public  void refuse();
//        public  void noAsk();
//    }
}
