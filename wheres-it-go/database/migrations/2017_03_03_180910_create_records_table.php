<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateRecordsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('records', function (Blueprint $table) {
            $table->increments('record_id');
            $table->string('record_title');
            $table->string('record_description')->nullable();
            $table->string('record_image')->nullable();
            $table->string('record_audio')->nullable();
            $table->unsignedInteger('category_id');
            $table->foreign('category_id')->references('category_id')->on('categories')->onDelete('cascade');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    
    
    public function down()
    {
        Schema::dropIfExists('records');
    }
}
