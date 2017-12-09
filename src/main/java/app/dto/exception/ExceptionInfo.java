package app.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by GW95IB on 2017-05-26.
 */
@AllArgsConstructor
@Getter
public class ExceptionInfo {

    private String requestUri;

    private String exceptionName;

    private String exceptionMessage;

}
