package dev.anarchy.translate.util;
import java.io.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;


@SuppressWarnings("serial")
public class CustomPrettyPrinter extends DefaultPrettyPrinter {
	
	private static final String objectFieldValueSeparator = ": ";
	private static final String objectEntrySeparator = ", ";

    public CustomPrettyPrinter() {
		this.indentArraysWith(new DefaultIndenter("\t", DefaultIndenter.SYS_LF));
		this.indentObjectsWith(new DefaultIndenter("\t", DefaultIndenter.SYS_LF));
    }

    public CustomPrettyPrinter(CustomPrettyPrinter base) {
    	super();
        _arrayIndenter = base._arrayIndenter;
        _objectIndenter = base._objectIndenter;
        _nesting = base._nesting;
    }

    @Override
    public CustomPrettyPrinter createInstance() {
        return new CustomPrettyPrinter(this);
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(objectFieldValueSeparator);
    }

    @Override
    public void writeObjectEntrySeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(objectEntrySeparator);
        _objectIndenter.writeIndentation(jg, _nesting);
    }
}