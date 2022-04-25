package io.sorted.command;

import io.sorted.app.MainVerticle;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Description;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.spi.launcher.DefaultCommand;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

@Name("-v")
@Summary("Report the version of the application")
@Description("Prints the version of the application according to the maven artifact.")
public class AppVersionCommand extends DefaultCommand {
  @Override
  public void run() throws CLIException {
    Optional<String> version = getVersion();
    if (version.isPresent()) {
      System.out.println(version.get());
    } else {
      System.out.println("Cannot find version");
    }
  }

  public static Optional<String> getVersion() {
    String version = MainVerticle.class.getPackage().getImplementationVersion();
    return Objects.nonNull(version) ? Optional.of(version) : pomVersion();
  }

  // parse the version from the POM file.
  public static Optional<String> pomVersion() {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      VersionParser handler = new VersionParser();
      saxParser.parse(new File("pom.xml"), handler);
    } catch (FoundVersionException version) {
      return Optional.of(version.getMessage());
    } catch (ParserConfigurationException | IOException | SAXException e) {
      System.out.println(e.getMessage());
      return Optional.empty();
    }
    return Optional.empty();
  }

  // custom parser to exit immediately upon getting the version by throwing a custom exception
  public static class VersionParser extends DefaultHandler {
    private final StringBuilder str = new StringBuilder();
    private final Stack<String> stack = new Stack<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      stack.push(qName);
      str.delete(0, str.length());
    }
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      if (stack.pop().equals("version") && stack.peek().equals("project") && stack.size() == 1) {
        throw new FoundVersionException(str.toString());
      }
      str.delete(0, str.length());
    }
    @Override
    public void characters(char[] ch, int start, int length) {
      str.append(ch, start, length);
    }
  }

  public static class FoundVersionException extends SAXException {
    public FoundVersionException(String s) {
     super(s);
    }
  }
}
