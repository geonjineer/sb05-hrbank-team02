package com.sprint.project.hrbank;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.project.hrbank.converter.ClientIpResolver;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientIpResolverTest {

  @Test
  void forwarded_ipv4_port_is_stripped() {
    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addHeader("Forwarded", "for=1.2.3.4:5678; proto=http; by=proxy");
    String ip = ClientIpResolver.resolve(req);
    assertThat(ip).isEqualTo("1.2.3.4");
  }

  @Test
  void forwarded_ipv6_in_brackets_is_unwrapped() {
    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addHeader("Forwarded", "for=\"[2001:db8::1]\"");
    String ip = ClientIpResolver.resolve(req);
    assertThat(ip).isEqualTo("2001:db8::1");
  }

  @Test
  void xForwardedFor_takes_first_and_strips_port_for_ipv4() {
    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addHeader("X-Forwarded-For", "9.8.7.6:443, 5.4.3.2");
    String ip = ClientIpResolver.resolve(req);
    assertThat(ip).isEqualTo("9.8.7.6");
  }

  @Test
  void fallback_remoteAddr_loopback_ipv6_normalized_to_ipv4() {
    MockHttpServletRequest req = new MockHttpServletRequest();
    req.setRemoteAddr("::1"); // 또는 "0:0:0:0:0:0:0:1"
    String ip = ClientIpResolver.resolve(req);
    assertThat(ip).isEqualTo("127.0.0.1");
  }
}
