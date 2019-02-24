<?php

use Illuminate\Database\Seeder;

class CategoriesTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        //
        DB::table('categories')->insert([
            'category_title' => 'bedroom',
            'category_description' => 'My bedroom',
            'user_id' => '1',
        ]);
        
        DB::table('categories')->insert([
            'category_title' => 'test2',
            'category_description' => 'test2',
            'user_id' => '1',
        ]);
    }
}
