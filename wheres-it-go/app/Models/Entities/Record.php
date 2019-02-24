<?php

namespace App\Models\Entities;

use Illuminate\Database\Eloquent\Model;

class Record extends Model
{
    protected $table = 'records';
    protected $primaryKey = 'record_id';
    
    protected $fillable = [
        'record_title', 
        'record_description', 
        'record_image', 
        'record_audio',
        'category_id'
    ];
    
    public function category()
    {
        return $this->belongsTo('App\Models\Entities\Category', 'category_id');
    }
}
