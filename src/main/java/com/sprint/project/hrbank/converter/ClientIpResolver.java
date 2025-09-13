package com.sprint.project.hrbank.converter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ClientIpResolver {
  private ClientIpResolver() {}

  public static String resolve(HttpServletRequest request) {
    // 1) RFC 7239 Forwarded: for=...
    String fwd = request.getHeader("Forwarded");
    if (fwd != null && !fwd.isBlank()) {
      Matcher m = Pattern.compile("for=([^;]+)", Pattern.CASE_INSENSITIVE).matcher(fwd);
      if (m.find()) {
        String v = m.group(1).replace("\"", "");   // 따옴표 제거
        if (v.startsWith("[") && v.endsWith("]")) {// [IPv6] 대괄호 제거
          v = v.substring(1, v.length() - 1);
        }
        // IPv4:port만 포트 제거 (IPv6은 그대로)
        int idx = v.indexOf(':');
        if (idx > 0 && v.contains(".")) v = v.substring(0, idx);
        return normalizeLoopback(v);
      }
    }

    // 2) X-Forwarded-For: 첫 값이 원 클라 IP
    String xff = request.getHeader("X-Forwarded-For");
    if (xff != null && !xff.isBlank()) {
      String v = xff.split(",")[0].trim();
      int idx = v.indexOf(':');
      if (idx > 0 && v.contains(".")) v = v.substring(0, idx); // IPv4:port → IP만
      return normalizeLoopback(v);
    }

    // 3) 폴백
    return normalizeLoopback(request.getRemoteAddr());
  }

  private static String normalizeLoopback(String ip) {
    if (ip == null) return null;
    return ("::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) ? "127.0.0.1" : ip;
  }
}
