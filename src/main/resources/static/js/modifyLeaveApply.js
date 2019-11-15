$(document).ready(function () {
    $('#startime').datepicker({setDate: new Date(), dateFormat: 'yy-mm-dd'});
    $('#endtime').datepicker({setDate: new Date(), dateFormat: 'yy-mm-dd'});

    $("#dept").hide();
    let grid = $("#grid-data").bootgrid({
        navigation: 2,
        columnSelection: false,
        ajax: true,
        url: "/leave/updatetasklist",
        formatters: {
            "taskcreatetime": function (column, row) {
                return row.taskcreatetime;
            },
            "commands": function (column, row) {
                return "<button class=\"btn btn-xs btn-default ajax-link command-run1\" data-row-id=\"" + row.taskid + "\">处理</button>";
            }
        }
    }).on("loaded.rs.jquery.bootgrid", function () {/* Executes after data is loaded and rendered */
        grid.find(".command-run1").on("click", function (e) {
            let taskid = $(this).data("row-id");
            $.post("dealtask", {"taskid": taskid, "type": "leave"}, function (data) {
                let obj = data;
                $("#reason").val(obj.reason);
                $("#type").val(obj.leave_type);
                $("#userid").val(obj.user_id);
                $("#startime").val(obj.start_time);
                $("#endtime").val(obj.end_time);
                $("#applytime").val(obj.apply_time);
                $("form").attr("action", "update/leave/task/" + taskid);
            });
            $("#dept").show();
            $("#btn").click(function () {
                $.post("update/leave/task/" + taskid, $("form").serialize(), function (a) {
                    alert("处理成功");
                    LoadAjaxContent("modifyapply");
                });
            });
        });
    });
});

