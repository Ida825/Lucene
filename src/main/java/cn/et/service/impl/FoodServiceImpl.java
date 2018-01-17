package cn.et.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.et.service.FoodService;

@Service
public class FoodServiceImpl implements FoodService{
	//分词库的路径
	private String dir = "D:\\index";
	//创建分词器
	IKAnalyzer analyzer = new IKAnalyzer();
	
	@Autowired
	JdbcTemplate jdbc;
	

	
	public void write(){	
		try {
			//获取索引库存储的目录
			Directory directory = FSDirectory.open(new File(dir));
			//关联Lucene版本和当前分词器
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
			//传入目录和分词器
			IndexWriter writer = new IndexWriter(directory,config);
			
			//获取数据库数据
			List<Map<String,Object>> foodList = getFood(); 
			//将数据库中的数据写入document对象中
			for (Map<String, Object> map : foodList) {
				//创建document对象
				Document document = new Document();
				document.add(new Field("foodname",map.get("foodname").toString(), TextField.TYPE_STORED));
				System.out.println(document+"=====");
				writer.addDocument(document);
				
			}
			
			writer.commit();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
	public List<Map<String,Object>> getFood() {
		List<Map<String,Object>> foodList = jdbc.queryForList("select * from Food");		
		return foodList;
	}


	
	public List<String> search(String foodname) {
		List<String> list= new ArrayList<String>();
		//获取索引库的存储目录
		try {
			Directory directory = FSDirectory.open(new File(dir));
			DirectoryReader reader = DirectoryReader.open(directory);
			//搜索类
			IndexSearcher searcher = new IndexSearcher(reader);
			//构建查询解析器 用于指定查询的属性名和分词器
			QueryParser parser = new QueryParser(Version.LUCENE_47,"foodname",analyzer);
			//开始搜索
			Query query = null;
			try {
				query = parser.parse(foodname);
				//获取搜索的结果 指定返回的document个数
				ScoreDoc[] hits = searcher.search(query, null,10).scoreDocs;
				for(int i=0;i<hits.length;i++){
					Document hitDoc = searcher.doc(hits[i].doc);
					//将分词结果存入list
					list.add(hitDoc.getField("foodname").stringValue());
					
					//查看分词方式
					Configuration config = DefaultConfig.getInstance();
					config.setUseSmart(false);
					IKSegmenter ik = new IKSegmenter(new StringReader(hitDoc.getField("foodname").stringValue()), config);
					Lexeme le = null;
					while((le = ik.next()) != null){
						System.out.println(le.getLexemeText());
					}
				}
				
				reader.close();
				directory.close();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	
}
