package slimefinder.io.properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

import slimefinder.io.CLI;
import slimefinder.util.Position;

import static slimefinder.util.FormatHelper.LN;
import static org.junit.Assert.*;

public class PropertyLoaderTest {

    private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();

    private CLI cli;
    private ByteArrayOutputStream out;
    private ByteArrayInputStream in;
    private AbstractProperties properties;
    private PropertyLoader loader;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(stdout));
        cli = new CLI();
        loader = new PropertyLoader(cli);
        out = new ByteArrayOutputStream();
        properties = new AbstractProperties("test") {
            @Override
            protected void setDefaults() {
                defaultValues.put("property1", 1);
                defaultValues.put("property2", 2);
            }
        };
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
    }



/*_____________________________________________ Reading ______________________________________________________________*/


    @Test
    public void doesntPrintWarningsWhenGeneratingPropertiesForTheFirstTime() {
        in = null; // InputStream is null when the property file doesn't exist

        loader.readProperties(properties, in);

        cli.flush();
        String str = stdout.toString();
        assertEquals(
            LN,
            str
        );
    }

    @Test
    public void doesntPrintWarningsWhenAllPropertiesArePresent() {
        in = new ByteArrayInputStream((
            "#comment" + LN +
            "property1=20" + LN +
            "property2=56" + LN
        ).getBytes());

        loader.readProperties(properties, in);

        cli.flush();
        String str = stdout.toString();
        assertEquals(
            LN + "Successfully loaded properties from file: '" + properties.filename + "'",
            str
        );
    }

    @Test
    public void printWarningsForUnusedProperties() {
        in = new ByteArrayInputStream((
            "#comment" + LN +
            "property1=20" + LN +
            "property2=56" + LN +
            "property3=unused" + LN
        ).getBytes());

        loader.readProperties(properties, in);

        cli.flush();
        String str = stdout.toString();
        assertEquals(
            LN + "Successfully loaded properties from file: '" + properties.filename + "'" +
            LN + "WARNING: Unused property, 'property3' in '" + properties.filename + "'",
            str
        );
    }

    @Test
    public void printsWarningsForMissingProperties() {
        in = new ByteArrayInputStream((
            "#comment" + LN +
            "property2=56" + LN
        ).getBytes());

        loader.readProperties(properties, in);

        cli.flush();
        String str = stdout.toString();
        assertEquals(
            LN + "Successfully loaded properties from file: '" + properties.filename + "'" +
            LN + "WARNING: property1 not specified. Using default (1)",
            str
        );
    }

    @Test
    public void defaultValuesAreSetCorrectly() {
        properties = new AbstractProperties("test") {
            @Override
            protected void setDefaults() {
                defaultValues.put("propertyInt", 43);
                defaultValues.put("propertyLong", 43L);
                defaultValues.put("propertyBoolean", true);
                defaultValues.put("propertyPosition", new Position(43, 21));
                defaultValues.put("propertyString", "test");
            }
        };
        in = null;

        loader.readProperties(properties, in);

        assertEquals(new Integer(43), properties.getInt("propertyInt"));
        assertEquals(new Long(43), properties.getLong("propertyLong"));
        assertEquals(true, properties.getBoolean("propertyBoolean"));
        assertEquals(new Position(43, 21).block, properties.getPosition("propertyPosition").block);
        assertEquals("test", properties.getString("propertyString"));
    }

    @Test
    public void customValuesAreSetCorrectly() {
        properties = new AbstractProperties("test") {
            @Override
            protected void setDefaults() {
                defaultValues.put("propertyInt", 0);
                defaultValues.put("propertyLong", 0L);
                defaultValues.put("propertyBoolean", false);
                defaultValues.put("propertyPosition", new Position(0, 0));
                defaultValues.put("propertyString", "default");
            }
        };
        in = new ByteArrayInputStream((
            "#comment" + LN +
            "propertyInt=43" + LN +
            "propertyLong=43" + LN +
            "propertyBoolean=true" + LN +
            "propertyPosition=43,21" + LN +
            "propertyString=test" + LN
        ).getBytes());

        loader.readProperties(properties, in);

        assertEquals(new Integer(43), properties.getInt("propertyInt"));
        assertEquals(new Long(43), properties.getLong("propertyLong"));
        assertEquals(true, properties.getBoolean("propertyBoolean"));
        assertEquals(new Position(43, 21).block, properties.getPosition("propertyPosition").block);
        assertEquals("test", properties.getString("propertyString"));
    }


/*_____________________________________________ Writing ______________________________________________________________*/


    @Test
    public void defaultValuesAreSavedInTheCorrectOrder() throws IOException {
        properties = new AbstractProperties("test") {
            @Override
            protected void setDefaults() {
                defaultValues.put("property8", 8);
                defaultValues.put("property2", 2);
                defaultValues.put("property3", 3);
            }
        };
        in = null;

        loader.readProperties(properties, in);
        loader.writeProperties(properties, out);
        String str = removeComments(out);
        assertEquals(
            "property8=8" + LN +
            "property2=2" + LN +
            "property3=3" + LN,
            str
        );

    }

    @Test
    public void successfullyReadingPropertiesOverridesDefaults() throws IOException {
        in = new ByteArrayInputStream((
            "#comment" + LN +
            "property1=20" + LN
        ).getBytes());

        loader.readProperties(properties, in);
        loader.writeProperties(properties, out);
        String str = removeComments(out);
        assertEquals(
            "property1=20" + LN +
            "property2=2" + LN,
            str
        );
    }

    private String removeComments(ByteArrayOutputStream out) {
        String lines[] = out.toString().split(LN);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null) continue;
            if (lines[i].length() == 0) continue;
            if (lines[i].charAt(0) != '#') builder.append(lines[i] + LN);
        }
        return builder.toString();
    }

}
