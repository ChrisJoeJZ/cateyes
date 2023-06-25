package cateye.chris.es;

import org.frameworkset.tran.DataStream;
import org.frameworkset.tran.db.input.es.DB2ESImportBuilder;

public class ESImport {
    public static void main(String[] args) {
        DB2ESImportBuilder importBuilder = DB2ESImportBuilder.newInstance();
        // JDBC 配置
        importBuilder.setDbName("cateyes_data")
                .setDbDriver("com.mysql.cj.jdbc.Driver")
                .setDbUrl("jdbc:mysql://localhost:3306/cateyes_data?useUnicode=true&characterEncoding=utf8&useCursorFetch=true")
                .setDbUser("root")
                .setDbPassword("123456")
                .setUsePool(false); //是否使用连接池

        // 将查询结果导入 ElasticSearch ** 设置es导入的数据源
//        importBuilder.setSql("select * from `film`");
//        importBuilder.setSql("select * from `cinema`");    //从mysql中导入影院cinema表的数据
          importBuilder.setSql("select * from `comment`");    //从mysql中导入影院comment表的数据
        // ElasticSearch 配置 ** 设置es导入的目标数据
        importBuilder
//                .setIndex("film")                            //导入film表结构
//                .setIndex("cinema")                            //导入cinema表结构
                .setIndex("comment")                            //导入comment表结构
                .setIndexType("_doc")
                .setRefreshOption(null)
                .setUseJavaName(false)
                .setBatchSize(5000)           //设置批量导入的记录数
                .setJdbcFetchSize(10000);     //设置从MySQL中批量读取的记录数
        // 开始导入数据
        DataStream dataStream = importBuilder.builder();
        dataStream.execute();
    }
}