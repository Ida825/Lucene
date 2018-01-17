package cn.et;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.parser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneIndexTest {
	//index文件库地址
	static String dir = "D:\\index";
	//定义分词器IKAnalyzer
	static IKAnalyzer analyzer = new IKAnalyzer();
	public static void main(String[] args) throws Exception {
		//http://lucene.apache.org/core/
		//http://archive.apache.org/dist/lucene/java/
		//write();
		search();
		String str = "张三疯是鼓浪屿最有名的咖啡奶茶店";
		//true为选择智能划分（北京师范大学），而false为最细粒度划分（北京师范大学，北京，京师，师范大学，师范，大学）
		//IKTokenizer token = new IKTokenizer(new StringReader(str), false);
		Configuration config = DefaultConfig.getInstance();
		config.setUseSmart(true);
		IKSegmenter ik = new IKSegmenter(new StringReader(str), config);
		Lexeme le = null;
		while((le = ik.next()) != null){
			System.out.print(le.getLexemeText()+"||");
		}
		
	}
	
	
	
	
	/**
	 * 创建索引库
	 * @throws IOException
	 */
	public static void write() throws IOException{
		//获取索引库的存储目录
		Directory directory = FSDirectory.open(new File(dir));
		//关联lucene版本和当前分词器
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		//传入目录和分词器
		IndexWriter writer = new IndexWriter(directory, config);
		
		//创建document对象
		Document doc = new Document();
		//往document 对象中 添加多个field属性
		Field field = new Field("name", "张三疯",TextField.TYPE_STORED);
		doc.add(field);
		field = new Field("desc", "张三疯是鼓浪屿最有名的咖啡奶茶店",TextField.TYPE_STORED);
		doc.add(field);
		//将对象写入索引库
		writer.addDocument(doc);
		
		Document doc1 = new Document();
		Field field1 = new Field("name", "猪头三",TextField.TYPE_STORED);
		doc1.add(field1);
		field1 = new Field("desc","猪头三是鼓浪屿一家卖肉脯的店",TextField.TYPE_STORED);
		doc1.add(field1);
		writer.addDocument(doc1);
		
		//提交事务
		writer.commit();
		//关流
		writer.close();		
	}
	
	/**
	 * 搜索
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws org.apache.lucene.queryparser.classic.ParseException 
	 */
	public static void search() throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException{
		//获取索引库的存储目录
		Directory directory = FSDirectory.open(new File(dir));
		DirectoryReader reader = DirectoryReader.open(directory);
		//搜索类
		IndexSearcher searcher = new IndexSearcher(reader);
		//构建查询解析器  用于指定查询的属性名和分词器
		QueryParser parser = new QueryParser(Version.LUCENE_47, "desc", analyzer);
		//开始搜索
		Query query = parser.parse("浪屿");
		//获取搜索的结果 指定返回的document个数
		ScoreDoc[] hits = searcher.search(query,null,10).scoreDocs;
		for(int i=0;i<hits.length;i++){
			Document hitDoc = searcher.doc(hits[i].doc);
			System.out.println(hitDoc.getField("name").stringValue());
		}
		reader.close();
		directory.close();
	}
}
