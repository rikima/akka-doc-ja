/**
 * Copyright (C) 2009-2012 Typesafe Inc. <http://www.typesafe.com>
 */
package akka.docs.extension;

//#imports
import akka.actor.Extension;
import akka.actor.AbstractExtensionId;
import akka.actor.ExtensionIdProvider;
import akka.actor.ActorSystem;
import akka.actor.ActorSystemImpl;
import akka.util.Duration;
import com.typesafe.config.Config;
import java.util.concurrent.TimeUnit;

//#imports

import akka.actor.UntypedActor;
import org.junit.Test;

public class SettingsExtensionDocTestBase {

  //#extension
  public static class SettingsImpl implements Extension {

    public final String DB_URI;
    public final Duration CIRCUIT_BREAKER_TIMEOUT;

    public SettingsImpl(Config config) {
      DB_URI = config.getString(config.getString("myapp.db.uri"));
      CIRCUIT_BREAKER_TIMEOUT = Duration.create(config.getMilliseconds("myapp.circuit-breaker.timeout"),
          TimeUnit.MILLISECONDS);
    }

  }

  //#extension

  //#extensionid
  public static class Settings extends AbstractExtensionId<SettingsImpl> implements ExtensionIdProvider {
    public final static Settings instance = new Settings();

    public Settings lookup() {
      return Settings.instance;
    }

    public SettingsImpl createExtension(ActorSystemImpl system) {
      return new SettingsImpl(system.settings().config());
    }
  }

  //#extensionid

  //#extension-usage-actor
  public static class MyActor extends UntypedActor {
    final SettingsImpl settings = Settings.instance.get(getContext().system());
    Connection connection = connect(settings.DB_URI, settings.CIRCUIT_BREAKER_TIMEOUT);

    //#extension-usage-actor

    public Connection connect(String dbUri, Duration circuitBreakerTimeout) {
      return new Connection();
    }

    public void onReceive(Object msg) {
    }

  }

  public static class Connection {
  }

  @Test
  public void demonstrateHowToCreateAndUseAnAkkaExtensionInJava() {
    final ActorSystem system = null;
    try {
      //#extension-usage
      String dbUri = Settings.instance.get(system).DB_URI;
      //#extension-usage
    } catch (Exception e) {
      //do nothing
    }
  }

}
