package us.kbase.networks.adaptor.jdbc;

public class QueryPropertyPlaceholder implements Comparable<QueryPropertyPlaceholder>{
	int index;
	boolean isScalar;
	
	int argumentCount;
	int argumentStartIndex;
	
	public QueryPropertyPlaceholder(int index, boolean isScalar) {
		super();
		this.index = index;
		this.isScalar = isScalar;
		this.argumentCount = -1;
		this.argumentStartIndex = -1;
	}
	
	public void cleanArgumentInfo()
	{
		this.argumentCount = -1;
		this.argumentStartIndex = -1;
	}

	@Override
	public int compareTo(QueryPropertyPlaceholder o) {
		return index > o.index ? 1 : (index < o.index ? -1 :0);
	}

	public String toStringPattern() {
		StringBuffer sb = new StringBuffer(argumentCount*2);
		for(int i = 0; i < argumentCount; i++)
		{
			if(i > 0){
				sb.append(",");
			}
			sb.append("?");
		}
		return sb.toString();		
	}
}