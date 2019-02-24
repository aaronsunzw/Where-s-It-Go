<?php

namespace App\Http\Controllers;

use App\Models\Entities\Category;
use Illuminate\Http\Request;
use App\Models\Services\CategoryManager;
use App\Models\Services\UserManager;

class CategoryController extends Controller
{
    public function __construct(CategoryManager $categoryManager) {
        $this->CategoryManager = $categoryManager;
    } 


    public function postAllCategories(Request $request){
        $data['username']=$request->input('username');
        $data['password']=$request->input('password');
        $data['user_id']=$request->input('user_id');
        $response = $this->CategoryManager->retrieveCategories($data);
        return $response['json'];
        
    }
    
    public function postCreateCategories(Request $request){
        $data['username']=$request->input('username');
        $data['password']=$request->input('password');
        $data['user_id']=$request->input('user_id');
        $data['category_title']=$request->input('category_title');
        $data['category_description']=$request->input('category_description');
        $response = $this->CategoryManager->createCategory($data);
        return $response['json'];
        
    }
    
    public function postDeleteCategory(Request $request) {
        $data['username'] = $request->input('username');
        $data['password'] = $request->input('password');
        $data['category_id'] = $request->input('category_id');
        $data['user_id'] = $request->input('user_id');
        $response = $this->CategoryManager->deleteCategory($data);
        return $response['json'];
    }
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        //
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     *
     * @param  \App\Models\Entities\Category  $category
     * @return \Illuminate\Http\Response
     */
    public function show(Category $category)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  \App\Models\Entities\Category  $category
     * @return \Illuminate\Http\Response
     */
    public function edit(Category $category)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \App\Models\Entities\Category  $category
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, Category $category)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  \App\Models\Entities\Category  $category
     * @return \Illuminate\Http\Response
     */
    public function destroy(Category $category)
    {
        //
    }
}
