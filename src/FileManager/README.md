# 文件系统模拟设计

##设计内容 
 一个简单的二级文件系统。要求做到以下几点 <br/>
（1）可以实现下列几条命令（至少 4 条）。<br/>
````
    dir 列文件目录 
    create 创建文件
    delete 删除文件
    open 打开文件
    close 关闭文件
    read 读文件
    write 写文件
 ````
（2）列目录时要列出文件名、物理地址、保护码和文件长度。<br/>
（3）源文件可以进行读写保护。<br/>

## 算法原理
算法原理启发于计算机的总线式结构。系统维护一条虚拟的 NowPath 路径。以下算法
将通过这条 NowPath 路径找到文件或目录并对其进行相关操作。
（1）列文件目录（getFileList）： 根据 java.io.File 所提供的 listFile 方法读取 NowPath 路
径下的所有文件，处理产生的字符串并生成多个FileInfoBean对象，储存在哈希表中。用javaFX
所提供的 TableView 将 HashMap 的 FileInfoBean 信息显示在视图层。
（2）删除文件（delete）：封装了 java.io.File 提供的 delete 方法，方法将读取 NowPath
路径下文件或目录对其进行操作。如果此路径名表示目录，则目录必须为空才能删除。请注
意，File 类定义了 delete 一个方法，当一个文件不能被删除时，它会抛出一个 IOException 。
（3）创建文件（mkdir/newFile）：封装了 java.io.File 提供的 createNewFile() 方法，根
据视图层输入的创建信息和 NowPath 路径决定创建的是文件还是目录。
（4）打开/关闭文件：IE 提供了一个 url.dll 的动态链接库。url.dll 能够 IE 调用各种应用
来打开不同文件的实现，我们就可以在 java 程序中利用它。windows 系统提供了一个叫
Rundll32.exe 的文件，它的作用是执行 DLL 文件中的内部函数。结合 url.dll 和 rundll32.exe,可
以通过在命令行中启动相应程序打开相应文档
（5）读写文件：交付给系统应用程序。当有程序进行读写操作时，禁止对用户界面操
作。
（6）搜索文件：遍历目录下所有的文件（包括子文件夹），找出文件内容包括 key 字符
串的那些文件，添加到 ArrayList<File> 中

## 结果展示
