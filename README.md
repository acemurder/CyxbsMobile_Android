# cyxbsmobile_android

## 模块化注意事项

1. ButterKnife在module内使用要用R2.id.xxx来访问资源
 ```groovy
apply plugin: 'com.jakewharton.butterknife'
annotationProcessor "com.jakewharton:butterknife-compiler:$butter_knife_version"
```
2. 使用阿里的ARouter做路由,每一个module要配置
```groovy
javaCompileOptions {
    annotationProcessorOptions {
        arguments = [ moduleName : 'edit-your-module-name' ]
    }
}
annotationProcessor "com.alibaba:arouter-compiler:$arouter_compiler_version"
```
