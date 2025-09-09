package cn.master.hub.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * refresh_token 实体类。
 *
 * @author 11's papa
 * @since 2025-09-03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "refresh_token")
@Table("refresh_token")
public class RefreshToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Schema(description = "ID")
    private String id;

    /**
     * token
     */
    @Schema(description = "token")
    private String token;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private Instant expiryDate;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
