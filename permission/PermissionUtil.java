package com.example.qi.tt.permission;

import android.support.v4.app.FragmentActivity;
import android.util.Log;


/**
 * Created by Qi on 2018/12/20.
 */

public class PermissionUtil {
    static   public  void requestPermission(FragmentActivity activity, final RequestPermissionCallBack callback , String... permissions){
        Permissions ps= new Permissions(activity);
//        permission.onRequestPermissionsResult();
        ps.requestEach(activity,new PermissionsFragment.PermissionCallBack() {
            @Override
            public void onResult(Permission permission) {
                if (permission.granted){ // 同意
                    if(callback!=null){
                        Log.d("", "accept: ");
                        callback.accept();
                    }

                }else if (permission.shouldShowRequestPermissionRationale){// 用户拒绝了该权限，没有选中不在询问，
                    if (callback!=null){
                        callback.refuse();
                    }
                }else {// 用户拒绝了该权限，并且选中 不再询问
                    if (callback!=null){
                        callback.noAsk();
                    }
                }
            }
        },permissions);


    }

    /**
     *   回调
     */
    static public interface RequestPermissionCallBack {
        void accept();
        void refuse();
        void noAsk();
    }
}
