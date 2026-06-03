package com.course.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.course.dao.MainOrderDao;
import com.course.dao.TableDao;
import com.course.entity.TableEntity;
import com.course.enums.ResultCode;
import com.course.model.dto.TableStatusDto;
import com.course.model.request.TableRequest;
import com.course.model.response.ApiResponse;
import com.course.model.response.TableResponse;
import com.course.model.response.TableStatusResponse;
import com.course.utils.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class TableService {
    @Autowired
    private TableDao tableDao;

    @Autowired
    private MainOrderDao mainOrderDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public ApiResponse<Object> addTable() {

        TableEntity tableEntity = TableEntity.builder()
                .openedAt(null)
                .status("空閒")
                .code(UUID.randomUUID().toString())
                .build();

        tableDao.addTable(tableEntity);

        TableResponse tableResponse = toTableResponse(tableEntity);
        return ApiResponse.success(tableResponse);
    }

    public ApiResponse<Object> updateTable(Integer id, TableRequest req) {
        TableEntity tableEntity = tableDao.findById(id);
        if (tableEntity != null) {
            tableEntity.setStatus(req.getStatus());
            if (req.getClearOpenedAt()) {
                tableEntity.setOpenedAt(null);
            }
            tableDao.updateTable(tableEntity);
        } else {
            return ApiResponse.error(ResultCode.TABLE_NOT_EXIST);
        }
        return ApiResponse.success();
    }

    public ApiResponse<Object> updateTableStatus(Integer id, String status) {
        TableEntity tableEntity = tableDao.findById(id);
        if (tableEntity != null) {
            tableEntity.setStatus(status);
            tableDao.updateTable(tableEntity);
        } else {
            return ApiResponse.error(ResultCode.TABLE_NOT_EXIST);
        }

        TableResponse tableResponse = toTableResponse(tableEntity);

        return ApiResponse.success(tableResponse);
    }

    public ApiResponse<String> openTable(String code) {

        TableEntity tableEntity = tableDao.findByCode(code);
        if (tableEntity == null) {
            return ApiResponse.error(ResultCode.TABLE_NOT_EXIST);
        }

        if ("空閒".equals(tableEntity.getStatus())) {
            LocalDateTime now = LocalDateTime.now();

            tableEntity.setStatus("使用中");
            tableEntity.setOpenedAt(now);
            tableEntity = tableDao.updateTable(tableEntity);
        }

        String token = jwtUtil.generateTableToken(tableEntity);
        return ApiResponse.success(token);
    }

    public ApiResponse<Object> checkTable(Integer id, LocalDateTime openedAt) {
        TableEntity tableEntity = tableDao.findById(id);

        if (tableEntity == null) {
            return ApiResponse.error(ResultCode.TABLE_NOT_EXIST);
        }

        if (!tableEntity.getOpenedAt().isEqual(openedAt)) {
            return ApiResponse.error(ResultCode.NOT_SAME_GUEST);
        }

        return ApiResponse.success();

    }

    public ApiResponse<List<TableResponse>> getAllTable() {
        List<TableResponse> tableList = tableDao.findAll().stream().map((TableEntity tableEntity) -> {
            return toTableResponse(tableEntity);
        }).collect(Collectors.toList());

        return ApiResponse.success(tableList);
    }

    public ApiResponse<TableResponse> getTable(Integer id) {
        TableEntity tableEntity = tableDao.findById(id);
        if (tableEntity != null) {
            TableResponse tableResponse = TableResponse.builder()
                    .id(tableEntity.getId())
                    .status(tableEntity.getStatus())
                    .openedAt(tableEntity.getOpenedAt())
                    .qrCode(tableEntity.getCode())
                    .build();
            return ApiResponse.success(tableResponse);
        } else {
            return ApiResponse.error(ResultCode.TABLE_NOT_EXIST);
        }
    }

    public ApiResponse<TableStatusResponse> getTableStatus(Integer id) {
        TableEntity tableEntity = tableDao.findById(id);
        if (tableEntity == null) {
            return ApiResponse.error(ResultCode.TABLE_NOT_EXIST);
        }

        TableStatusDto tableStatusDto = mainOrderDao.getTableStatus(id);

        TableStatusResponse tableStatusResponse = TableStatusResponse.builder()
                .isOpened(tableStatusDto.isOpened())
                .orderCount(tableStatusDto.getOrderCount())
                .build();

        return ApiResponse.success(tableStatusResponse);
    }

    private TableResponse toTableResponse(TableEntity tableEntity) {
        TableResponse tableResponse = TableResponse.builder()
                .id(tableEntity.getId())
                .status(tableEntity.getStatus())
                .openedAt(tableEntity.getOpenedAt())
                .qrCode(tableEntity.getCode())
                .build();
        return tableResponse;
    }

}
