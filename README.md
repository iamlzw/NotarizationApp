# NotarizationApp
基于Hyperledger fabric的公证服务应用demo

测试步骤
### step 1:clone 代码,并移动到相应位置
```bash
# git clone https://github.com/iamlzw/NotarizationApp.git
```
仓库中共包括三个部分
##### 1.notarization,基于fabric java sdk开发的客户端代码.将该文件夹移动到fabric-samples/fabcar目录下
##### 2.deployCC.sh,基于原fabric-samples中test-network中部署智能合约脚本修改后的脚本.将该文件移动到fabric-samples/test-network/scripts/目录下,替换原有的deployCC.sh,记得备份.
##### 3.docrec.go,智能合约代码将该文件夹移动到fabric-samples/chaincode/目录下,并初始化go.mod
在docrec.go所在目录下执行一下命令
```bash
# export GO111MODULE=on go mod vendor
# go mod init github.com/src/hyperledger/fabric-samples/chaincode/docrec/go/
```
该目录下会多出go.mod文件,如果不执行这步,之后进行打包智能合约时会报错文件过大.
### step 2:启动网络
在fabric-samples/test-network目录下
```bash
# ./network.sh up createChannel -ca -s couchdb
```
![carbon80392075facaa9c1.png](http://lifegoeson.cn:8888/images/2020/08/07/carbon80392075facaa9c1.png)
一般像图片中那样,网络就启动成功,channel也创建成功
### step 3:安装实例化智能合约
```bash
# ./network.sh deployCC
```
![carbon1.png](http://lifegoeson.cn:8888/images/2020/08/07/carbon1.png)
一般像图片中那样,智能合约已经部署成功
### step 4:测试demo
在fabric-samples/fabcar/notarization目录下
```bash
# mvn test
```
![carbon2.png](http://lifegoeson.cn:8888/images/2020/08/07/carbon2.png)
测试成功
