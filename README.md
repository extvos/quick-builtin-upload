# quick-builtin-upload

基础文件上传服务。支持文件单个上传和以`Resumable.js`为实现的大文件切片上传。



## 接口

### 文件上传选项


**接口地址**:`/_builtin/upload-options`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取当前后台文件上传配置</p>



**请求参数**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- |
|200|OK|Result«UploadConfig»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- |
|code||integer(int32)|integer(int32)|
|count||integer(int64)|integer(int64)|
|data||UploadConfig|UploadConfig|
|&emsp;&emsp;chunkNumberParameterName||string||
|&emsp;&emsp;chunkSize||integer(int64)||
|&emsp;&emsp;chunkSizeParameterName||string||
|&emsp;&emsp;currentChunkSizeParameterName||string||
|&emsp;&emsp;fileNameParameterName||string||
|&emsp;&emsp;identifierParameterName||string||
|&emsp;&emsp;pathSegments||integer(int32)||
|&emsp;&emsp;prefix||string||
|&emsp;&emsp;relativePathParameterName||string||
|&emsp;&emsp;root||string||
|&emsp;&emsp;simultaneous||integer(int32)||
|&emsp;&emsp;temporary||string||
|&emsp;&emsp;totalChunksParameterName||string||
|&emsp;&emsp;totalSizeParameterName||string||
|&emsp;&emsp;typeParameterName||string||
|error||string||
|msg||string||
|page||integer(int64)|integer(int64)|
|pageSize||integer(int64)|integer(int64)|
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"count": 0,
	"data": {
		"chunkNumberParameterName": "",
		"chunkSize": 0,
		"chunkSizeParameterName": "",
		"currentChunkSizeParameterName": "",
		"fileNameParameterName": "",
		"identifierParameterName": "",
		"pathSegments": 0,
		"prefix": "",
		"relativePathParameterName": "",
		"root": "",
		"simultaneous": 0,
		"temporary": "",
		"totalChunksParameterName": "",
		"totalSizeParameterName": "",
		"typeParameterName": ""
	},
	"error": "",
	"msg": "",
	"page": 0,
	"pageSize": 0,
	"total": 0
}
```

### 文件上传


**接口地址**:`/_builtin/upload/{category}`


**请求方式**:`POST`


**请求数据类型**:`multipart/form-data`


**响应数据类型**:`*/*`


**接口描述**:<p>支持文件上传和切片上传。请勿使用Swagger测试，访问 <a target='_blank' href='_builtin/upload-test/index.html'>Upload Test</a></p>



**请求参数**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型 | 是否必须 | 数据类型 | schema |
| -------- | -------- | -------- | -------- | -------- | ------ |
| category | category | path     | true     | string   |        |
| file     | file     | formData | false    | file     |        |
| queries  | queries  | query    | false    |          | object |


**响应状态**:


| 状态码 | 说明         | schema             |
| ------ | ------------ | ------------------ |
| 200    | OK           | Result«UploadFile» |
| 201    | Created      |                    |
| 401    | Unauthorized |                    |
| 403    | Forbidden    |                    |
| 404    | Not Found    |                    |


**响应参数**:


| 参数名称             | 参数说明 | 类型           | schema         |
| -------------------- | -------- | -------------- | -------------- |
| code                 |          | integer(int32) | integer(int32) |
| count                |          | integer(int64) | integer(int64) |
| data                 |          | UploadFile     | UploadFile     |
| &emsp;&emsp;checksum |          | string         |                |
| &emsp;&emsp;filename |          | string         |                |
| &emsp;&emsp;prefix   |          | string         |                |
| &emsp;&emsp;root     |          | string         |                |
| &emsp;&emsp;size     |          | integer(int64) |                |
| &emsp;&emsp;url      |          | string         |                |
| error                |          | string         |                |
| msg                  |          | string         |                |
| page                 |          | integer(int64) | integer(int64) |
| pageSize             |          | integer(int64) | integer(int64) |
| total                |          | integer(int64) | integer(int64) |


**响应示例**:
```javascript
{
	"code": 0,
	"count": 0,
	"data": {
		"checksum": "",
		"filename": "",
		"prefix": "",
		"root": "",
		"size": 0,
		"url": ""
	},
	"error": "",
	"msg": "",
	"page": 0,
	"pageSize": 0,
	"total": 0
}
```
