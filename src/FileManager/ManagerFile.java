package FileManager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManagerFile {

    public static String listFile(File file) throws IOException{
        StringBuilder out = new StringBuilder();
        File[] files = file.listFiles();
        for (File f : files) {
            DateFormat df= new SimpleDateFormat("yyyy-MM-dd");
            String time = df.format(new Date(f.lastModified()));
            if (f.isFile()) {
                out.append(f.getName() + "||" + f.getPath() + "||" + f.length()/1024 + "||" + "文件" + "||"+ time + "\n");
            }
            if (f.isDirectory()) {
                out.append(f.getName() + "||" + f.getPath() + "||" +"目录" + "||" + time + "\n");
            }
        }
        return out.toString().trim();
    }

    public static boolean delete(File file) {

        return file.delete();
    }

    public static boolean mkdir(File file)  {
        if(file.exists()) return false;

        return file.mkdir();
    }

    public static Map<Integer, FileInfoBean> getFileList(File file) throws IOException{
        int count = 0;
        int tCount;
        String name,path,permission,size,line,lastTime,isFile;
        BufferedReader br = null;
        FileInfoBean fInfo;
        StringTokenizer st;
        Map<Integer, FileInfoBean> map = new HashMap<>();
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new ByteArrayInputStream(listFile(file).getBytes(StandardCharsets.UTF_8)), Charset.forName("UTF-8")));

            while ((line = br.readLine()) != null) {
                st = new StringTokenizer(line, "||");
                while (st.hasMoreElements()) {
                    tCount = st.countTokens();
                    if (tCount == 5) {
                        name = st.nextToken();
                        path = st.nextToken();
                        permission = "rw";
                        size = st.nextToken();
                        isFile = st.nextToken();
                        lastTime = st.nextToken();
                        fInfo = new FileInfoBean(name,path,isFile,permission,size,lastTime);
                        map.put(++count,fInfo);
                    }else if(tCount == 4) {
                        name = st.nextToken();
                        path = st.nextToken();
                        isFile = st.nextToken();
                        size = " ";
                        permission = "rw";
                        lastTime = st.nextToken();
                        fInfo = new FileInfoBean(name,path,isFile,permission,size,lastTime);
                        map.put(++count,fInfo);
                    }else {
                        break;
                    }
                }
            }

        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(br!=null)
                    br.close();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }


        return map;
    }

    public static boolean newFile(File tempFile) throws IOException {
        return tempFile.createNewFile();
    }

    public static List<File> searchFiles(File folder, final String key) {
        List<File> res = new ArrayList<>();
        if(folder.isFile()) {
            res.add(folder);
        }
        File[] subFolder = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile())
                    return true;
                if(pathname.getName().toLowerCase().contains(key))
                    return true;
                return  false;
            }
        });

        if (subFolder != null) {
            for (File file : subFolder) {
                if (file.isFile()) {
                    res.add(file);
                }else {
                    res.addAll(searchFiles(file,key));
                }
            }
        }
        return res;
    }
}
