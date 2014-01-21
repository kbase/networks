package us.kbase.kbasenetworks.adaptor;

public interface AdaptorFactory {
	public Adaptor buildAdaptor() throws AdaptorException;
}
