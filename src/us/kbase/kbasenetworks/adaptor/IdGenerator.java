package us.kbase.kbasenetworks.adaptor;

public class IdGenerator {

	
	public static final IdGenerator Dataset = new IdGenerator("kb|netdataset");
	public static final IdGenerator Network = new IdGenerator("kb|net");
	public static final IdGenerator Node = new IdGenerator("kb|netnode");
	public static final IdGenerator Edge = new IdGenerator("kb|netedge");
	
	
	private String prefix;
	private int id = 0;
	
	private IdGenerator(String prefix)
	{
		this.prefix = prefix;
	}
	
	public String nextId()
	{
		return prefix + "." + (id++);
	}
	
	public String toKBaseId(String adaptorPrefix, String localId)
	{
		return prefix + "." +  adaptorPrefix + "." + localId;
	}
	
	public static String toLocalId(String kbaseId)
	{
		int index = kbaseId.lastIndexOf(".");
		return index >= 0 ? kbaseId.substring(index+1) : kbaseId;
	}
}
