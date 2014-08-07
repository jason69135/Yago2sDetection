$.ajaxSetup({
	timeout: 120000,
});

function Channel() {

	this.jsonChannelInfo          = null;

	this.jsonOriginBatchColumnTag = null;
	this.jsonTargetBatchColumnTag = null;
	this.arrNormalColumn          = null;
	// this.jsonNormalColumnTag = null;
	this.strChannelDefault        = 'all';
	this.strChannelNow            = 'all';
	this.strBatchNow              = 'null';
	this.strFilter                = '';//'obj_num>0';
	this.STATUS_PASS              = 1;
	this.STATUS_DELETE            = 2;
	this.STATUS_NEW               = 0;
	this._init();
}

Channel.prototype._init = function() {
	this.getDataChannel();
	// this.getDataNormalColumnAndTag();
}

Channel.prototype.getDataChannel = function() {
	this.getData('channel', this.strFilter, this.strChannelDefault, 'null');

}

Channel.prototype.getDataColumnTag = function(channel, batch) {
	this.getData('column_tag', this.strFilter, channel, batch);
}


Channel.prototype.getData = function(type, filter, channel, batch) {

	var _this = this;
	$.ajax({
		type: 'GET',
		url:  './connect/get_channel_column_tag.php',
		data: {
				'data_type': type,
				'channel'  : channel,
				'batch'   : batch,
				// 'filter'   : filter,
				'show_type': 0,
		      },
		success: function(data) {

			console.log(data);
			if(type === 'channel') {
				_this.jsonChannelInfo = clone(data.channel);
				_this.writeChannel();
			} else if(type === 'column_tag') {
				_this.jsonOriginBatchColumnTag = clone(data.column_tag);
				_this.jsonTargetBatchColumnTag = clone(data.column_tag);
				_this.writeColumnTag();
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR.responseText);
		},
		dataType: 'json',
	});
}


// Channel.prototype.getDataNormalColumnAndTag = function() {
// 	var _this = this;
// 	$.ajax({
// 		type: 'GET',		
// 		url:  './connect/get_column_tag.php',
// 		async: 'false',
// 		success: function(data){
// 			console.log(data);
// 			_this.arrNormalColumn     = data.column;
// 			_this.jsonNormalColumnTag = data.col_tag

// 		},
// 		error: function(jqXHR, textStatus, errorThrown) {
// 			console.log(jqXHR.responseText);
// 		},		
// 		dataType: 'json',
// 	});
// }


Channel.prototype.writeChannel = function() {
	var jsonChannel = this.jsonChannelInfo;

	this.cleanPageContent();

	var head = "";
		head += "<thead>\n";
		head += "\t<tr>\n";
		head += "\t\t<th>site</th>\n";
		head += "\t\t<th>batch amount</th>\n";
		head += "\t\t<th>column amount</th>\n";
		head += "\t\t<<th>tag amount</th>\n";
		head += "\t\t<<th>operation</th>\n";
		head += "\t</tr>";
		head += "</thead>\n";		
	$(head).appendTo($('.table'));
	var dom = '<tbody>\n';
	var index = 0;
	for (var channel in jsonChannel) {
		var o = jsonChannel[channel];
		var btn = "<a onclick=changeChannel('" + channel + "') class='btn btn-small btn-primary'>查看详细</a>";
		dom += "\t<tr data-index="+ index +">\n";
		dom += "\t\t<td>" + channel + "</td>\n";
		dom += "\t\t<td>" + o.int_batch_num + "</td>\n";
		dom += "\t\t<td>" + o.int_column_num + "</td>\n";
		dom += "\t\t<td>" + o.int_tag_num + "</td>\n";
		dom += "\t\t<td>" + btn +"</td>\n";
		dom += "\t</tr>\n";

		index ++;

	}

	dom += '</tbody>\n';

	$(dom).appendTo($('.table')); 
}

Channel.prototype.writeColumnTag = function() {
	var arr_batch = this.jsonTargetBatchColumnTag.arr_batch;

	this.cleanPageContent();

	var head = "";
		head += "<thead>\n";
		head += "\t<tr>\n";
		head += "\t\t<th>batch</th>\n";
		head += "\t\t<th>original<br>column</th>\n";
		head += "\t\t<th>original<br>tag</th>\n";		
		head += "\t\t<th>online<br>column</th>\n";
		head += "\t\t<th>online<br>tag</th>\n";
		head += "\t\t<th>总数</th>\n";
		head += "\t\t<th>未审核</th>\n";		
		head += "\t\t<th>审核通过</th>\n";
		head += "\t\t<th>可上线</th>\n";
		head += "\t\t<th>删除</th>\n";


		head += "\t\t<<th>操作</th>\n";
		head += "\t</tr>";
		head += "</thead>\n";		

	$(head).appendTo($('.table'));

	var dom = '<tbody>\n';
	for (var batch in arr_batch) {
		var arr_column = arr_batch[batch].arr_column;
		for (var column in arr_column) {

			var arr_tag = arr_column[column].arr_tag;


			for (var tag in arr_tag) {
				var t = arr_tag[tag];

				var option = {
					'channel' : this.strChannelNow,
					'batch': batch,
					'original_column': column,
					'original_tag': tag,
					'online_column': t.str_online_column,
					'online_tag': t.str_online_tag,
					'exemption' : false,
				};
				var btn = "<div class='btn-group'>\n";
					btn += "<a onclick='showModalModifyColumnTag("+ $.toJSON(option) +")' class='btn btn-small btn-primary'>Map</a>\n";
					//btn += "<a onclick=remapColumnTag("+ $.toJSON(option) +") class='btn btn-small btn-danger'>Remap</a>\n";
					
					option.exemption = true;
					btn += "<a onclick='showModalModifyColumnTag("+ $.toJSON(option) +")' class='btn btn-small btn-danger'>免审核</a>\n";
					btn += "</div>\n";
				dom += "\t<tr data-index=" + batch + "-" + column + "-" + tag + ">\n";
				dom += "\t\t<td>" + batch + "</td>\n";
				dom += "\t\t<td>" + column + "</td>\n";
				dom += "\t\t<td>" + tag + "</td>\n";
				dom += "\t\t<td>" + t.str_online_column + "</td>\n";
				dom += "\t\t<td>" + t.str_online_tag + "</td>\n";
				dom += "\t\t<td>" + t.int_obj_num_all + "</td>\n";
				dom += "\t\t<td><a onclick='jumpToAuditPage("+ $.toJSON(option) +")' href='javascript:;'>" + t.int_obj_num_new + "</a></td>\n";
				dom += "\t\t<td>" + t.int_obj_num_passed + "</td>\n";
				dom += "\t\t<td>" + t.int_obj_num_releasable + "</td>\n";
				// dom += "\t\t<td>" + t.int_obj_num_released +"</td>\n";
				dom += "\t\t<td>" + t.int_obj_num_deleted +"</td>\n";
				dom += "\t\t<td>" + btn +"</td>\n";
				dom += "\t</tr>\n";
			}


		};
	}

	dom += '</tbody>\n';

	$(dom).appendTo($('.table')); 
}



Channel.prototype.changeChannelTo = function(channel) {

	if(channel === this.strChannelNow)
		return;

	this.cleanPageContent();
	$('#dis_batches').empty();

	if(channel === 'all') {
		$('.dis_channel').html('All Site');
		$('#batch').attr('style', 'visibility: hidden;');

		this.jsonOriginBatchColumnTag = null;
		this.jsonTargetBatchColumnTag = null;
		this.writeChannel();
	} else {
		$('.dis_channel').html(channel);
		$('#batch').attr('style', 'visibility: visible;');

		var batches = object_keys(this.jsonChannelInfo[channel].arr_batch);
		// this.strBatchNow = 'null';
		if(batches.length > 0) {
			this.strBatchNow = batches[0];
			$('#dis_batch span:first-child').html(this.strBatchNow);
			for (var i in batches) {
				$('#dis_batches').append('<li><a onclick=changeBatchTo("' + channel + '","' + batches[i] + '")>' + batches[i] + '</a></li>\n');
			}
			this.getDataColumnTag(channel, this.strBatchNow);

		}

	}

	this.strChannelNow = channel;
}

Channel.prototype.changeBatchTo = function(channel, batch) {
	if(batch =='null' || empty(batch) || batch == this.strBatchNow || empty(channel) || channel != this.strChannelNow)
		return;
	this.strBatchNow = batch;
	$('#dis_batch span:first-child').html(batch);

	this.getDataColumnTag(channel, batch);


}


Channel.prototype.cleanPageContent = function() {
	$('.table').empty();
	// $('#dis_batches').empty();
	// $('#dis_batch span:first-child').html('no batch');

}


// Channel.prototype.modifyColumn = function(option) {
// 	console.log(option);
// 	index     = option.index;
// 	channel   = option.channel;
// 	col_index = option.column_index;
// 	col_to    = option.column_to;

// 	this.jsonTargetChannel[channel][col_index].online_column = col_to;

// 	this.saveChanges('update');
// 	// var checkChannelColumn();
// 	// var change = this.objectDiff(o, t);

// 	// if(!empty(change))
// 	// 	saveChanges(change);

// 	$("tr[data-index='"+ index +"'] td:nth-child(3)").html(column_to);
// 	$('#modifyCloumn').modal('hide');
// }
Channel.prototype.showModalModifyColumnTag  = function (option) {
	var channel         = option.channel;
	var batch           = option.batch;
	var original_column = option.original_column;
	var original_tag    = option.original_tag;
	var online_column   = option.online_column;
	var online_tag      = option.online_tag;
	var exemption       = option.exemption;
	var status_from     = (exemption == true) ? 0 : 1;

	$('#mapFormBody').empty();
	
	var dom = "";
		dom += "<input type='hidden' name='option' value='"+ $.toJSON(option)+"'>";
		dom += "<input type='hidden' name='status_from' value=" + status_from + ">";
		dom += "<input type='hidden' name='status_to' value=3>";
		dom += "<div class='control-group'>";
		dom += "\t<label class='control-label' for='channel'>channel</label>";
		dom += "\t<div class='controls'>";
		dom += "\t\t<span class='input-xlarge uneditable-input'>"+ channel +"</span>";
		dom += "\t\t<input class='uneditable-input' name='channel' type='hidden' value='"+ channel +"'>";
		dom += "\t</div>";
		dom += "</div>"
		dom += "<div class='control-group'>";
		dom += "\t<label class='control-label' for='batch'>batch</label>";
		dom += "\t<div class='controls'>";
		dom += "\t\t<span class='input-xlarge uneditable-input'>"+ batch +"</span>";
		dom += "\t\t<input class='uneditable-input' name='batch' type='hidden' value='"+ batch +"'>";
		dom += "\t</div>";
		dom += "</div>"
		dom += "<div class='control-group'>";
		dom += "\t<label class='control-label' for='original_column'>original_column</label>";
		dom += "\t<div class='controls'>";
		dom += "\t\t<span class='input-xlarge uneditable-input'>"+ original_column +"</span>";
		dom += "\t\t<input class='uneditable-input' name='original_column' type='hidden' value='"+ original_column +"'>";
		dom += "\t</div>";
		dom += "</div>"
		dom += "<div class='control-group'>";
		dom += "\t<label class='control-label' for='original_tag'>original_tag</label>";
		dom += "\t<div class='controls'>";
		dom += "\t\t<span class='input-xlarge uneditable-input'>"+ original_tag +"</span>";
		dom += "\t\t<input class='uneditable-input' name='original_tag' type='hidden' value='"+ original_tag +"'>";
		dom += "\t</div>";
		dom += "</div>"
		dom += "<div class='control-group'>";
		dom += "\t<label class='control-label' for='online_column'>online_column</label>";
		dom += "\t<div class='controls'>";
		dom += "\t\t<input class='input-xlarge' name='online_column' type='text' placeholder='请正确填写线上column'>";
		dom += "\t\t<span class='help-inline'>必填</span>"
		dom += "\t</div>";
		dom += "</div>"				
		dom += "<div class='control-group'>";
		dom += "\t<label class='control-label' for='online_tag'>online_tag</label>";
		dom += "\t<div class='controls'>";
		dom += "\t\t<input class='input-xlarge' name='online_tag' type='text' placeholder='请正确填写线上tag'>";
		dom += "\t\t<span class='help-inline'>必填</span>"
		dom += "\t</div>";
		dom += "</div>"

	$(dom).appendTo($('#mapFormBody'));
	$('#modifyCloumnTag').modal('show');


}


// Channel.prototype.modifyColumnTag = function(option) {
// 	console.log(option);
// 	var channel         = option.channel;
// 	var batch           = option.batch;
// 	var original_column = option.original_column;
// 	var original_tag    = option.original_tag;
// 	var column_to       = option.column_to;
// 	var tag_to          = option.tag_to;


// 	this.jsonTargetBatchColumnTag['arr_batch'][batch]['arr_column'][original_column]['arr_tag'][original_tag].online_column = column_to;
// 	this.jsonTargetBatchColumnTag['arr_batch'][batch]['arr_column'][original_column]['arr_tag'][original_tag].online_tag = tag_to;
// 	this.saveChanges('update');
// 	$("tr[data-index='"+ column_origin + "-" + tag_index +"'] td:nth-child(3)").html(column_to);
// 	$("tr[data-index='"+ column_origin + "-" + tag_index +"'] td:nth-child(4)").html(tag_to);
// 	$('#modifyCloumnTag').modal('hide');
// }


// Channel.prototype.remapColumnTag = function(option) {

// 	if(empty(option.online_column) || empty(option.online_tag)) {
// 		alert("不能对未创立映射规则的项目进行remap");
// 		return;
// 	}
// 	var change = new Array();
// 	var temp = new Object();
// 	temp.column = option.column;
// 	temp.index = option.tag_index;
// 	temp.field = ['online_column', 'online_tag'];
// 	change.push(temp);

// 	this._saveChanges('update', 'channel_column_tag', change, option);
// }


// Channel.prototype.checkChangesChannelColumn = function() {
// 	var o = this.jsonOriginChannel;
// 	var t = this.jsonTargetChannel;

// 	var result = this.objectDiff(o, t);

// 	this.jsonOriginChannel = clone(t);

// 	return result;
// }

Channel.prototype.checkChangesChannelColumnTag = function() {
	if (this.strChannelNow === this.strChannelDefault)
		return;

	var o = this.jsonOriginBatchColumnTag;
	var t = this.jsonTargetBatchColumnTag;

	var result = this.objectDiff(o, t);

	this.jsonOriginBatchColumnTag = clone(t);
	return result;
}

Channel.prototype.objectDiff = function(origin, target) {
	// k == channel
	var result = new Array();

	for(var k in target) {
		var arrO = origin[k];
		var arrT = target[k];

		// o,t == channel -> columns , is array
		for (var i in arrT) {
			var field = new Array();	
			var changed = false;
			var o = arrO[i];
			var t = arrT[i];
			for (var j in t) {
				if(o[j] !=t[j]) {
					changed = true;
					field.push(j);
				}
			}

			if (changed) {
				var tt = new Object();
				tt.column = k;
				tt.index = i;
				tt.field = field;
				result.push(tt);
			}

		}
	}

	return result;
}


Channel.prototype.saveChanges = function(type) {
	if(this.strChannelNow === this.strChannelDefault) {
		this.saveChangesChannelColumn(type);
	} else {
		this.saveChangesChannelColumnTag(type);
	}
}

Channel.prototype.saveChangesChannelColumn = function(type) {
	var change = this.checkChangesChannelColumn();

	if(empty(change)) 
		return;

	this._saveChanges(type, 'channel_column', change);
}

Channel.prototype.saveChangesChannelColumnTag = function(type) {
	var change = this.checkChangesChannelColumnTag();

	if(empty(change)) 
		return;

	this._saveChanges(type, 'channel_column_tag', change);

}

Channel.prototype._saveChanges = function(query_type, data_type, change, option) {


	var channel_all = null;
	var column_tag_all = null;
	var exemption = false;
	switch(data_type) {
		case 'channel_column':
			channel_all = this.jsonTargetChannel;
			// console.log('channel_column');
			break;
		case 'channel_column_tag':
			column_tag_all = this.jsonTargetBatchColumnTag;
			// console.log('channel_column_tag');
			break;
	}

	if(!empty(option))
		exemption = option.exemption;
	console.log({
			      'query_type'    : query_type,
			      'data_type'     : data_type,
			      'channel'       : this.strChannelNow,
			      'channel_all'   : $.toJSON(channel_all),
			      'column_tag_all': $.toJSON(column_tag_all),
			      'change'        : change,
			      'exemption'     : exemption,

		      });
	$.ajax({
		type: 'POST',
		url:  './connect/save_channel_column_tag.php',
		data: {
			      'query_type'    : query_type,
			      'data_type'     : data_type,
			      'channel'       : this.strChannelNow,
			      'channel_all'   : $.toJSON(channel_all),
			      'column_tag_all': $.toJSON(column_tag_all),
			      'change'        : change,
			      'exemption'    : exemption,

		      },
		success: function(data) {
			console.log(data);

			if(data.status == 200)
			{
				// for(var i in change) {
				// 	var col_tag = change[i];
				// 	index = col_tag.column + "-" + col_tag.index;
				// 	$("tr[data-index='"+ index +"'] td:nth-child(7)").html(0);
				// 	$("tr[data-index='"+ index +"'] td:nth-child(8)").html();

				// }

			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log(jqXHR.responseText);
		},	
	});
}



