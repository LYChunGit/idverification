# IdVerification
一个自定义相机拍照，用于金融系列认证

使用
* 1、导入 compile 'me.lyc.IdVerification:idverificationlib:1.1.0'
* 2、记得权限申请
```java 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermission = new RxPermissions(testActivity.this);
            rxPermission.requestEach(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,//SD卡写入
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE//读外部存储的权限
            )
                    .subscribe(new io.reactivex.functions.Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) throws Exception {
                            if (permission.granted) {
                                // 用户已经同意该权限

                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                Toast.makeText(testActivity.this, "您已关闭了SD卡写入以及读取权限，暂无法使用！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(testActivity.this, "您已关闭了SD卡写入以及读取权限，暂无法使用！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
```
* 3、使用
```java 
   String path = App.getInstance().getExternalCacheDir().getPath() + "/ID_CARD_POSITIVE.png";
  //SelectImageAndCamera 1、Context 2、FragmentManager 3、图片返回的类型标志 4、图片拍照时候提示框5、你要拍完图片放在哪里
   CameraActivity.SelectImageAndCamera(testActivity.this,getSupportFragmentManager(), 1, R.mipmap.camera_front, path);
```
* 4、在onActivityResult 接收返回值
```java 
      @Override
         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
             if (resultCode != RESULT_OK) {
                 return;
             }
             String imagePath = data.getStringExtra(RESULT_IMAGER);
             int imageType = data.getIntExtra(CAMERA_ACTIVITY_TYPE,0);
             if (!TextUtils.isEmpty(imagePath)){
                 Toast.makeText(testActivity.this,"拍照图片地址:"+imagePath+" 返回图片类型："+imageType,Toast.LENGTH_LONG).show();
             }else  if (data.getData()!=null){
                 Toast.makeText(testActivity.this,"选择图片地址:"+data.getData()+" 返回图片类型："+imageType,Toast.LENGTH_LONG).show();
             }
             super.onActivityResult(requestCode, resultCode, data);
         }
```
* 5、![Image text](https://raw.github.com/idverification/img/one.jpg)
![Image text](https://raw.github.com/idverification/img/two.jpg)


