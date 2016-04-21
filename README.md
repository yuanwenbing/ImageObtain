# ImageObtain

这个库是为获取本机或者相机图片封装的，因为适配原因，有的图片通过相机拍照后onActivityResult这个方法返回的数据data为空，所以通过一个折中方法，将拍照后的图片都存在SD卡某处，然后再去取出图片的形式，以兼容某些机器。

另外如果程序中有这么多地方用到照相机，每个类都需要一大堆代码来处理这些事情 ，所以对代码进行封装，下面方法可以通过系统相机返回一个image path，但注意如果设置setCorpEnable为false也就是不进行裁剪，则corpHeight和corpWidth同时失效。如果需要从图为库获取，只需要setChannel(ImageObtainInstance.ImageChannel.CHANNEL_ALBUM)
即可实现。

` new ImageObtainInstance(this)
`	                .setCorpEnable(true)
	                .setCorpHeight(200)
	                .setCorpWidth(300)
	                .setPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "obtain_test")
	                .setChannel(ImageObtainInstance.ImageChannel.CHANNEL_CAMERA)
	                .setObtainListener(new ImageObtainInstance.OnPictureObtainListener() {
	                    @Override
	                    public void obtainSuccess(String path) {
	                        Bitmap bitmap = BitmapFactory.decodeFile(path);
	                        mImageView.setImageBitmap(bitmap);
	                    }
	
	                    @Override
	                    public void obtainFailure() {
	
	                    }
	                }).obtain();

SD权限需要添加，程序没有对6.0的运行时权限进行适配。在6.0的手机上可能取不到SD卡，所以需要自己做适配。有一个简单的权限封装（[TedPermission][1]）大家可以参考，本文也参考这种写法实现。

[1]:	https://github.com/ParkSangGwon/TedPermission