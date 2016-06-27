java -jar xxx.jar help
# 执行爬虫
java -jar  xxx.jar crawler
#执行 文件入mongo
java -jar  xxx.jar intoMongo busAndStationFile|jobsFile|parkingFile|busAndStationFile|subwayAndBusFile|(or file path) collectionName
java -jar  xxx.jar intoMongo busAndStationFile|jobsFile|parkingFile|busAndStationFile|subwayAndBusFile|(or file path) ip port database collection printcount regex
# regex 只在 fang 有效 并赋值为 fangCode
# 执行  mongo 入 mysql
java -jar xxx.jar mongoIntoMysql busAndStation|parking|subwayAndBus|subwayAndJob|buspoi|fang
# 执行 搜房 mongo数据更新
java -jar xx.jar intoMongUpdate fangUpdate|(or file path) ip port database collection printcount
java -jar xx.jar intoMongUpdate fangUpdate|(or file path) collectionName

# 公司信息入库
java -jar xxx.jar intoMongoCompany companFilePath jobFilePath outputFilePath ip port database collection printcount


intoMongoCompany f:\zj\companyDesc-2014-12-11.txt f:\zj\companyDescJobDesc-2014-12-11.txt f:\zj\companyEnd-2014-12-11.txt 192.168.1.4 27017 demo company51job 100