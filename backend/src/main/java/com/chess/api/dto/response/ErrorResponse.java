package com.chess.api.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    String type,
    String message,
    LocalDateTime timestamp,
    String path,
    Map<String, Object> details
) {}
