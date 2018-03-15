# RNO 生成全国行政区域数据的工具

代码管理：http://svn.hgicreate.com/srcsvn/rno/rno-tool/rno-area-tool

## 安装所需的 Intellij IDEA 插件
### 1.安装 Lombok 插件
使用 Lombok 插件，可以方便的查看由 Lombok 生成的类定义和类的方法。

 1.打开 File > Settings... > Plugins > Browser repositories... 插件仓库页面。

2.输入 Lombok 关键字进行搜索。

3.选择 Lombok Plugin，点击 Install 按钮进行安装。

4.重启 Intellij IDEA 生效。

## 文件格式
### 1.输入源CSV文件
表头及内容示例如下。
<pre>
id,"code","name",parent_id,"first_letter",level
19,"440000","广东省",0,"G",0
20,"450000","广西壮族自治区",0,"G",0
</pre>

### 2.输出CSV文件
表头及内容示例如下。
<pre>
id,name,level,parent_id,longitude,latitude
440000,广东省,1,0,113.7633,23.3790
440100,广州市,2,440000,113.2644,23.1291
440106,天河区,3,440100,113.3612,23.1247
</pre>


## 系统部署
运行命令：
<pre> java -jar rno-area-tool-0.0.1-SNAPSHOT.jar E:\input-area.csv E:\output-area.csv </pre>
