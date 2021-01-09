@/*
     underline : 是否有下标分界线
     id: textarea域id
     name : textarea域名称
     row: 初始化textarea域的高
@*/
<div class="form-group">
    <label class="col-sm-3 control-label lanClass" data-lanid="${lanId}">${name}</label>
    <div class="col-sm-9">
        <textarea class="form-control" id="${id}" name="${id}" rows="${row}"
                  @if(isNotEmpty(disabled)){
                  disabled="${disabled}"
                  @}
        >@if(isNotEmpty(value)){
${value}@}
</textarea>
    </div>
</div>
@if(isNotEmpty(underline) && underline == 'true'){
<div class="hr-line-dashed"></div>
@}


