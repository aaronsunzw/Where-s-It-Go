<?php

namespace App\Models\Entities;

use Illuminate\Database\Eloquent\Model;

class Category extends Model
{
    //
    protected $table = 'categories';
    protected $primaryKey = 'category_id';
    
    protected $fillable = [
        'category_title', 
        'category_description', 
        'user_id'
    ];
    
    public function records()
    {
        return $this->hasMany('App\Models\Entities\Record');
    }
    
    public function user()
    {
        return $this->belongsTo('App\Models\Entities\User', 'user_id');
    }
}
