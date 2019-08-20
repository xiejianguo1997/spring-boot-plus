/**
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.geekidea.springbootplus.upload.web;

import io.geekidea.springbootplus.common.api.ApiResult;
import io.geekidea.springbootplus.config.core.SpringBootPlusProperties;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 上传控制器
 * @author geekidea
 * @date 2019/8/20
 * @since 1.2.1-RELEASE
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private SpringBootPlusProperties springBootPlusProperties;

    /**
     * 上传单个文件
     */
    @PostMapping("/")
    @ApiOperation(value = "上传单个文件",notes = "上传单个文件",response = ApiResult.class)
    public ApiResult<Boolean> upload(@RequestParam("img") MultipartFile multipartFile) throws Exception{
        log.info("multipartFile = " + multipartFile);
        log.info("ContentType = " + multipartFile.getContentType());
        log.info("OriginalFilename = " + multipartFile.getOriginalFilename());
        log.info("Name = " + multipartFile.getName());
        log.info("Size = " + multipartFile.getSize());

        InputStream inputStream = multipartFile.getInputStream();
        // 文件保存目录
        File saveDir = new File(springBootPlusProperties.getUploadPath());
        // 判断目录是否存在，不存在，则创建，如创建失败，则抛出异常
        if (!saveDir.exists()){
            boolean flag = saveDir.mkdirs();
            if (!flag){
                throw new RuntimeException("创建" +saveDir + "目录失败！");
            }
        }
        String srcFileName = multipartFile.getOriginalFilename();
        String fileExtension= FilenameUtils.getExtension(srcFileName);
        // 这里可自定义文件名称，比如按照业务类型/文件格式/日期
        // 此处按照文件日期存储
        String dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssS"));
        log.info("dateString = " + dateString);

        String saveFileName = dateString + "." +fileExtension;
        log.info("saveFileName = " + saveFileName);
        File saveFile = new File(saveDir,saveFileName);
        // 保存文件到服务器指定路径
        FileUtils.copyToFile(inputStream,saveFile);

        // 上传成功之后，返回访问路径，请根据实际情况设置

        String fileAccessPath = springBootPlusProperties.getResourceAccessUrl() + saveFileName;
        log.info("fileAccessPath:{}",fileAccessPath);

        return ApiResult.ok(fileAccessPath);
    }

}