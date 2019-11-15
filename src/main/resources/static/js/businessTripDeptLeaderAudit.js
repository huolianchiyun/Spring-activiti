$(document).ready(function () {
    $("#dept").hide();
    let grid = $("#grid-data").bootgrid({
        navigation: 2,
        columnSelection: false,
        ajax: true,
        url: "/businessTrip/depttasklist",
        formatters: {
            "taskcreatetime": function (column, row) {
                return row.taskcreatetime;
            },
            "commands": function (column, row) {
                return "<button class=\"btn btn-xs btn-default ajax-link command-run1\" data-row-id=\"" + row.taskid + "\">处理</button>";
            }
        }

    }).on("loaded.rs.jquery.bootgrid", function () {
        grid.find(".command-run1").on("click", function (e) {
            let taskid = $(this).data("row-id");
            $.post("dealtask", {"taskid": taskid,"type": "businessTrip"}, function (data) {
                debugger
                let obj = data;
                $("#reason").val(obj.reason);
                $("#userid").val(obj.user_id);
                $("#startime").val(obj.start_time);
                $("#endtime").val(obj.end_time);
                $("#applytime").val(obj.apply_time);
                $("form").attr("action", "task/deptcomplete/" + taskid);
            });
            $("#dept").show();
            $("#btn").click(function () {
                $.post("task/deptcomplete/" + taskid, $("form").serialize(), function (a) {
                    alert("处理成功");
                    LoadAjaxContent("businessTripDeptLeaderAudit");
                });
            });
        });
    });
});
