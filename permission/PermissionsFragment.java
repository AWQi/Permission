package com.example.qi.tt.permission;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


public class PermissionsFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 42;

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private boolean mLogging;
    private  PermissionCallBack callBack = null;

    public PermissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
    }
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSIONS_REQUEST_CODE) return;
        /**
         *  若 有 callback 则 对  结果 进行处理
         */
        if (callBack!=null){
                if (isGrantedAllPermission(permissions)){ //  获取到 全部 权限
                        callBack.onResult(new Permission("permission",true,false));
                }else if(isNotShowPermissionRationale(permissions)){ // 有拒绝权限，且不再提示的
                        callBack.onResult(new Permission("permission",false,false));
                }else {// 拒绝权限，但可以提示再次申请获取
                        callBack.onResult(new Permission("permission",false,true));
                }
        }
    }

    public void onRequestPermissionsResult(Permission permission) {
        if (callBack!=null){
            callBack.onResult(permission);
        }
    }
    /**
     *  检测 是否有 没获取到权限且不提示的  如果有返回 true
     * @param permissions
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean isNotShowPermissionRationale(String permissions[]) {
        for (String p:permissions){
            if (!shouldShowRequestPermissionRationale(p)&&!isGranted(p)){
                return  true;
            }
        }
        return  false;
    }

    /**
     *  检测 权限是否全部获取到，如果 全部获取到  返回 true
     * @param permissions
     * @return
     */
    private   boolean isGrantedAllPermission(String permissions[]) {
        /**
         *  是否 获得权限
         */
        for (String p:permissions) {
            if (!isGranted(p)){
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        final FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            throw new IllegalStateException("This fragment must be attached to an activity.");
        }
        return fragmentActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        final FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            throw new IllegalStateException("This fragment must be attached to an activity.");
        }
        return fragmentActivity.getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    public void setLogging(boolean logging) {
        mLogging = logging;
    }


    void log(String message) {
        if (mLogging) {
            Log.d(Permissions.TAG, message);
        }
    }

    public PermissionCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(PermissionCallBack callBack) {
        this.callBack = callBack;
    }


    public interface PermissionCallBack {

        void onResult(Permission permission);

    }

}
