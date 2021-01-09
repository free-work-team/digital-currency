package com.jyt.terminal.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.toolkit.IOUtils;
import com.jyt.terminal.commom.enums.BitException;
import com.jyt.terminal.commom.enums.BizExceptionEnum;
import com.jyt.terminal.support.StrKit;


public class FileUtil {

    private static Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * NIO way
     */
    public static byte[] toByteArray(String filename) {

        File f = new File(filename);
        if (!f.exists()) {
            log.error("文件未找到！" + filename);
            throw new BitException(BizExceptionEnum.FILE_NOT_FOUND);
        }
        FileChannel channel = null;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
                // do nothing
                // System.out.println("reading");
            }
            return byteBuffer.array();
        } catch (IOException e) {
            throw new BitException(BizExceptionEnum.FILE_READING_ERROR);
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                throw new BitException(BizExceptionEnum.FILE_READING_ERROR);
            }
            try {
                fs.close();
            } catch (IOException e) {
                throw new BitException(BizExceptionEnum.FILE_READING_ERROR);
            }
        }
    }

    /**
     * 删除目录
     *
     * @author fengshuonan
     * @Date 2017/10/30 下午4:15
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
	/**
	 * 将文件转成base64 字符串
	 * 
	 * @param path文件路径
	 * @return 
	 * @throws IOException
	 */
	public static String encodeBase64File(String path) throws IOException  {
		if (StrKit.isBlank(path)) {
			throw new IOException(path + "文件不存在");
		}
		byte[] b = Files.readAllBytes(Paths.get(path));
		return Base64.getEncoder().encodeToString(b);
	}
	
	/**
	 * 将文件进行Base64解密
	 * @param base64
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static void decryptByBase64(String base64, String filePath) throws IOException{
		if (StrKit.isBlank(base64)  || StrKit.isBlank(filePath)) {
			throw new IOException( "参数为空值,base64:" + base64 + ", filePath:" + filePath);
		}
		Files.write(Paths.get(filePath), Base64.getDecoder().decode(base64), StandardOpenOption.CREATE);
	}
	
	/**
	 * 校验文件是否存在
	 * @param filename
	 */
	public static void verifyFileExists(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            log.error("文件[{}]未找到！", filename);
            throw new BitException(BizExceptionEnum.FILE_NOT_FOUND);
        }
    }
	
	/**
	 * 文件批量压缩
	 * @param parms 每个Map 含有两个key，分别是filePath（文件全路径）、fileName（文件名。若不传，则取文件路径中的文件名），需保证fileName策略唯一。
	 * @param byteOutPutStream 字节输出流，不可空
	 */
	public static void batchFileToZIP(List<Map<String, String>> parms, ByteArrayOutputStream byteOutPutStream) {
		if(byteOutPutStream == null) {
			return;
		}
		if(ToolUtil.isEmpty(parms)) {
			return;
		}
		ZipOutputStream zipOut = new ZipOutputStream(byteOutPutStream);
		FileInputStream inputStream = null;
		try {
			for (Map<String, String> value : parms) {
				String filePath = value.get("filePath");
				if (ToolUtil.isEmpty(filePath)) {
					continue;
				}
				// 文件路径
				inputStream = new FileInputStream(new File(filePath));
				// 使用指定名称创建新的 ZIP 条目
				String fileName = value.get("fileName");
				if (ToolUtil.isEmpty(fileName)) {
					fileName = FilenameUtils.getName(filePath);
				}
				ZipEntry zipEntry = new ZipEntry(fileName);
				// 开始写入新的 ZIP 文件条目并将流定位到条目数据的开始处
				zipOut.putNextEntry(zipEntry);
				byte[] b = new byte[1024];
				int length = 0;
				while ((length = inputStream.read(b)) != -1) {
					zipOut.write(b, 0, length);
				}
				zipOut.closeEntry();
			}
		} catch (FileNotFoundException e) {
			log.error("文件未找到!原因:{}", e.getMessage());
			throw new BitException(BizExceptionEnum.FILE_NOT_FOUND);
		} catch (IOException e) {
			log.error("文件压缩失败!原因:{}", e.getMessage());
			throw new BitException(BizExceptionEnum.FILE_READING_ERROR);
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(zipOut);
		}
	}
}