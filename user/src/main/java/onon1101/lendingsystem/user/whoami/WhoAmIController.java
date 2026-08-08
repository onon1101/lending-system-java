package onon1101.lendingsystem.user.whoami;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import onon1101.lendingsystem.configurations.context.user.CurrentUserContext;
import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;

import onon1101.lendingsystem.configurations.controller.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "auth", description = "認證相關 API")
@RestController
@RequestMapping("/api/v1/auth")
public class WhoAmIController {

    private final CurrentUserProvider currentUserProvider;

    public WhoAmIController(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Operation(
            summary = "取得目前登入使用者",
            description = "依據 Bearer Access Token 取得目前登入使用者的公開資料。")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "成功取得目前登入使用者",
                    useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "未提供、無效或已過期的 Access Token",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "使用者帳號不是啟用狀態",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "系統未預期錯誤",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/whoami")
    public ResponseEntity<ApiResponse<WhoAmIResponse>> whoAmI() {
        CurrentUserContext currentUser = currentUserProvider.getCurrentUser();

        WhoAmIResponse response =
                new WhoAmIResponse(
                        currentUser.publicUserId(),
                        currentUser.username(),
                        currentUser.email(),
                        currentUser.status());

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, response));
    }
}
