package FileManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static FileManager.ManagerFile.*;

public class FileManagerApp extends Application {
    TableView<FileInfoBean> tableView = new TableView<>();
    ObservableList<FileInfoBean> observableList = FXCollections.observableArrayList();
    String nowPath = "e:";
    String lastPath = " ";
    String searchStr;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("资源管理器");
        BorderPane  bp = new BorderPane();
        VBox root = new VBox();
        HBox hBox = new HBox();
        Scene scene = new Scene(bp,1000,600);
        Menu file = new Menu("文件");
        Menu edit = new Menu("编辑");
        Menu tools = new Menu("工具");
        Menu look = new Menu("查看");
        Menu help = new Menu("帮助");
        MenuBar menuBar = new MenuBar();

        //文件子菜单
        MenuItem newDirMItem = new MenuItem("新建文件夹");
        newDirMItem.setOnAction(new NewDirEvent<>());
        MenuItem newFileMItem = new MenuItem("新建文件");
        newFileMItem.setOnAction(new NewFileEvent<>());
        MenuItem deleteFile = new MenuItem("删除");
        deleteFile.setOnAction(new DeleteFileEvent<>());
        file.getItems().addAll(newFileMItem,newDirMItem,deleteFile);

        //查看子菜单
        MenuItem refreshBtn = new MenuItem("刷新");
        refreshBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    deleteDate();
                    addData(nowPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        look.getItems().addAll(refreshBtn);


        //编辑子菜单
        MenuItem reCopy = new MenuItem("撤销复制");
        MenuItem recover = new MenuItem("恢复");
        MenuItem copy = new MenuItem("复制");
        MenuItem selectAll = new MenuItem("全选");
        edit.getItems().addAll(selectAll,copy,reCopy,recover);

        //工具子菜单
        MenuItem N = new MenuItem("映射网络驱动器");
        MenuItem D = new MenuItem("断开网络驱动器");
        MenuItem S = new MenuItem("打开同步中心");
        tools.getItems().addAll(N,D,S);

        //帮助子菜单
        MenuItem aboutBtn = new MenuItem("关于");
        aboutBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("关于作者");
            alert.setHeaderText("桂林理工大学信息学院");
            alert.setContentText("软件工程17-3班 | 刘康富");
            alert.showAndWait();
        });
        help.getItems().addAll(aboutBtn);

        menuBar.getMenus().addAll(file,edit,look,tools, help);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        TableColumn TCName = new TableColumn("名称");
        TCName.setMinWidth(200);
        TCName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        TableColumn TCPer = new TableColumn("权限");
        TCPer.setMinWidth(50);
        TCPer.setCellValueFactory(new PropertyValueFactory<>("permission"));
        TableColumn TCTime = new TableColumn("修改时间");
        TCTime.setMinWidth(100);
        TCTime.setCellValueFactory(new PropertyValueFactory<>("lastTime"));
        TableColumn TCSize = new TableColumn("大小");
        TCSize.setMinWidth(200);
        TCSize.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        TableColumn TCPath = new TableColumn("路径");
        TCPath.setMinWidth(400);
        TCPath.setCellValueFactory(new PropertyValueFactory<>("filePath"));

        TableColumn TCIsFile = new TableColumn("类型");
        TCIsFile.setMinWidth(50);
        TCIsFile.setCellValueFactory(new PropertyValueFactory<>("isFile"));

        addData(nowPath);

        tableView.setItems(observableList);
        tableView.setRowFactory(tv -> {
            TableRow<FileInfoBean> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 1 && (!row.isEmpty())) {
                    FileInfoBean p = row.getItem();
                    lastPath = getParePath(nowPath);
                    nowPath = p.getFilePath();
                }else if(event.getClickCount() == 2 && (!row.isEmpty()) && row.getItem().getIsFile().equals("文件")) {
                    FileInfoBean p = row.getItem();
                    nowPath = p.getFilePath();
                    Thread open = new Thread(){
                        @Override
                        public void run() {
                            try {
                                Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler " + nowPath);
                                //System.out.println(nowPath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    open.start();
                }else if(event.getClickCount() == 2 && (!row.isEmpty()) && row.getItem().getIsFile().equals("目录")) {
                    FileInfoBean p = row.getItem();
                    lastPath = getParePath(nowPath);
                    nowPath = p.getFilePath();

                    deleteDate();
                    try {
                        addData(nowPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

        tableView.getColumns().addAll(TCName,TCIsFile,TCPer,TCSize,TCTime,TCPath);
        Button newFileBtn = new Button("新建文件");
        newFileBtn.setOnAction(new NewFileEvent<>());

        Button newDirBtn = new Button("新建文件夹");
        newDirBtn.setOnAction(new NewDirEvent<>());

        Button openFileBtn = new Button("打开");
        openFileBtn.setOnAction(new OpenFileEvevt<>());

        Button lastDir = new Button("后退");
        lastDir.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(lastPath.equals(" ")) return;
                deleteDate();
                try {
                    nowPath = getParePath(nowPath);
                    addData(nowPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button deleteBtn = new Button("删除");
        deleteBtn.setOnAction(new DeleteFileEvent<>());

        TextField input = new TextField();
        input.setPromptText("在这里输入");
        Button search = new Button("搜索");

        search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchStr = input.getText();
                System.out.println(searchStr);
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        List<File> res = searchFiles(new File(getParePath(nowPath)),searchStr);
                        deleteDate();
                        String name;
                        String path;
                        String permission;
                        String size;
                        String isFile;

                        Iterator<File> c = res.iterator();
                        while (c.hasNext()) {
                            DateFormat df= new SimpleDateFormat("yyyy-MM-dd");

                            File f = c.next();
                            String time = df.format(new Date(f.lastModified()));
                            //f 为文件
                            if (f.isFile()) {
                                name = f.getName();
                                path = f.getPath();
                                permission = "rw";
                                size = String.valueOf(f.length()/1024);
                                isFile = "文件";
                                FileInfoBean fileInfoBean = new FileInfoBean(name,path,isFile,permission,size,time);
                                observableList.add(fileInfoBean);
                            }

                            if(f.isDirectory()) {
                                name = f.getName();
                                path = f.getPath();
                                permission = "rw";
                                size = " ";
                                isFile = "目录";
                                FileInfoBean fileInfoBean = new FileInfoBean(name,path,isFile,permission,size,time);
                                observableList.add(fileInfoBean);
                            }
                        }

                    }
                };
                t.start();
            }
        });

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(input,search,openFileBtn,deleteBtn,newFileBtn,newDirBtn,lastDir);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(8,8,8,0));
        root.getChildren().addAll(menuBar);

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
            }
        });
        bp.setTop(root);
        bp.setCenter(tableView);
        bp.setBottom(hBox);
        primaryStage.setIconified(false);
        primaryStage.show();
    }


   // 删除数据
    private void deleteDate() {
        for(int i = 0; i < tableView.getItems().size(); i++) {
            tableView.getItems().clear();
        }
    }

    //添加数据
    private void addData(String path) throws IOException{
        Map map = ManagerFile.getFileList(new File(path));
        for(int i = 0;i < map.size(); i++) {
            FileInfoBean fInfo = (FileInfoBean) map.get(i+1);
            if(fInfo == null) continue;
            observableList.add(fInfo);
        }
    }

    //获取父路径
    private String getParePath(String nowPath) {
        return StringUtils.substringBeforeLast(nowPath,"\\");
    }


    //文件删除事件
    private  class DeleteFileEvent<ActionEvent extends Event> implements  EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            //将光标所在行移出列表并在物理磁盘中删除。
            boolean su = delete(new File(nowPath));
            if (su == false ) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("错误");
                alert.setContentText("文件夹不为空");
                return;
            }
            deleteDate();
            try {
                addData(getParePath(nowPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //新建文件事件
    private class NewFileEvent<ActionEvent extends Event> implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("新建文件");
            dialog.setContentText("");
            dialog.setHeaderText("");
            Optional<String> result = dialog.showAndWait();
            //判断输入是否为空
            if(result.isPresent() && result.get().isEmpty()) {
                Alert error = new Alert(Alert.AlertType.ERROR, "输入不能为空！");
                error.showAndWait();
            }
            //输入不为空时，检查输入格式是否正确
            if(result.isPresent() && !result.get().isEmpty()) {
                String path = nowPath+"\\"+result.get();
                File tempFile = new File(path);
                Thread tempThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            ManagerFile.newFile(tempFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        FileInfoBean temp = new FileInfoBean(result.get(),
                                path,
                                "文件","rw","0",new Date().toString());
                        observableList.add(temp);
                    }
                };
                tempThread.start();
            }
        }
    }

    //文件打开事件
    private class OpenFileEvevt<ActionEvent extends Event> implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent event) {
                if(nowPath != null){
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler " + nowPath);
                                //System.out.println(nowPath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();
                }
            }
    }

    //新建文件夹事件
    private class NewDirEvent<ActionEvent extends Event> implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("新建文件夹");
            dialog.setContentText("");
            dialog.setHeaderText("");
            Optional<String> result = dialog.showAndWait();
            //判断输入是否为空
            if(result.isPresent() && result.get().isEmpty()) {
                Alert error = new Alert(Alert.AlertType.ERROR, "输入不能为空！");
                error.showAndWait();
            }
            //输入不为空时，检查输入格式是否正确
            if(result.isPresent() && !result.get().isEmpty()) {
                File tempFile = new File(nowPath+"\\"+result.get());
                Thread tempThread = new Thread() {
                    @Override
                    public void run() {
                        mkdir(tempFile);
                        FileInfoBean temp = new FileInfoBean(result.get(),
                                    nowPath,
                                    "目录","rw"," ",new Date().toString());
                        observableList.add(temp);
                    }
                };
                tempThread.start();
            }
        }
    }
}

